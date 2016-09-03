package cn.hugeterry.updatefun.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.utils.GetAppInfo;
import cn.hugeterry.updatefun.utils.InstallApk;
import cn.hugeterry.updatefun.utils.StorageUtils;
import cn.hugeterry.updatefun.view.DownLoadDialog;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/15 16:41
 */
public class Download extends Thread {

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static int progress;

    private Down_handler handler;

    private static int length;
    private static int count;

    private Context context;
    private static File apkFile;

    public Download(Context context) {
        this.context = context;
        handler = new Down_handler(context);
    }

    public Download(Context context, Notification.Builder builder) {
        this.context = context;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        handler = new Down_handler(context, builder, notificationManager);
    }

    private static class Down_handler extends Handler {
        WeakReference<Context> mContextReference;
        Notification.Builder builder;
        NotificationManager notificationManager;

        Down_handler(Context context) {
            mContextReference = new WeakReference<>(context);
        }

        Down_handler(Context context, Notification.Builder builder, NotificationManager notificationManager) {
            mContextReference = new WeakReference<>(context);
            this.builder = builder;
            this.notificationManager = notificationManager;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            Context context = mContextReference.get();
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (UpdateKey.DialogOrNotification == 1) {
                        ((DownLoadDialog) context).progressBar.setProgress(progress);
                        ((DownLoadDialog) context).textView.setText(progress + "%");
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setProgress(length, count, false)
                                .setContentText("下载进度:" + progress + "%");
                        notificationManager.notify(1115, builder.build());
                    }
                    break;
                case DOWN_OVER:
                    if (UpdateKey.DialogOrNotification == 1) {
                        ((DownLoadDialog) context).finish();
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setTicker("下载完成");
                        notificationManager.notify(1115, builder.build());
                        notificationManager.cancel(1115);
                    }
                    length = 0;
                    count = 0;
                    DownloadKey.TOShowDownloadView = 1;
                    if (DownloadKey.ISManual) {
                        DownloadKey.LoadManual = false;
                    }
                    if (checkApk(context)) {
                        Log.i("UpdateFun TAG", "APK路径:" + apkFile);
                        InstallApk.startInstall(context, apkFile);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public void run() {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            url = new URL(DownloadKey.apkUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i("UpdateFun TAG",
                String.format("ApkDownloadUrl:%s", DownloadKey.apkUrl));
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            length = conn.getContentLength();
            is = conn.getInputStream();
        } catch (FileNotFoundException e0) {
//            e0.printStackTrace();
            try {
                conn.disconnect();
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.connect();
                String location = new String(conn.getHeaderField("Location").getBytes("ISO-8859-1"), "UTF-8").replace(" ", "");
                url = new URL(location);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                length = conn.getContentLength();
                is = conn.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }


        try {
            File file = StorageUtils.getCacheDirectory(context);
            if (!file.exists()) {
                file.mkdir();
            }

            apkFile = new File(file, DownloadKey.saveFileName);
            FileOutputStream fos = new FileOutputStream(apkFile);
            long tempFileLength = file.length();
            byte buf[] = new byte[1024];
            int times = 0; //这很重要
            int numread;
            do {
                numread = is.read(buf);
                count += numread;
                progress = (int) (((float) count / length) * 100);
                if ((times == 512) || (tempFileLength == length)) {
                    handler.sendEmptyMessage(DOWN_UPDATE);
                    times = 0;
                }
                times++;
                if (numread <= 0) {
                    handler.sendEmptyMessage(DOWN_OVER);
                    break;
                }
                fos.write(buf, 0, numread);
            } while (!DownloadKey.interceptFlag);// 点击取消就停止下载.
            fos.flush();
            fos.close();
            is.close();
            conn.disconnect();

            if (DownloadKey.interceptFlag) {
                Log.i("UpdateFun TAG", "interrupt");
                length = 0;
                count = 0;
                DownloadKey.interceptFlag = false;
                this.interrupt();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    private static boolean checkApk(Context context) {
        String apkName = GetAppInfo.getAPKPackageName(context, apkFile.toString());
        String appName = GetAppInfo.getAppPackageName(context);
        if (apkName.equals(appName)) {
            Log.i("UpdateFun TAG", "apk检验:包名相同,安装apk");
            return true;
        } else {
            Log.i("UpdateFun TAG",
                    String.format("apk检验:包名不同。该app包名:%s，apk包名:%s", appName, apkName));
            Toast.makeText(context, "apk检验:包名不同,不进行安装,原因可能是运营商劫持", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
