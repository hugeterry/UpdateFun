package cn.hugeterry.updatefun.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/15 16:41
 */
public class Download extends Thread {

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Notification.Builder builder;
    private NotificationManager mNotificationManager = null;

    private Context context;
    private ProgressBar progressBar;

    private int length;
    private int count;


    public Download(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;

    }

    public Download(Context context, Notification.Builder builder, NotificationManager mNotificationManager) {
        this.context = context;
        this.builder = builder;
        this.mNotificationManager = mNotificationManager;
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (UpdateKey.DialogOrNotification == 1) {
                        progressBar.setProgress(progress);
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setProgress( length, count, false);
                        mNotificationManager.notify(1115, builder.build());
                    }
                    break;
                case DOWN_OVER:
                    if (UpdateKey.DialogOrNotification == 1) {
                        ((Activity) context).finish();
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setTicker("下载完成");
                        mNotificationManager.notify(1115, builder.build());
                        NotificationManager manger = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                        manger.cancel(1115);
                    }
                    DownloadKey.TOShowDownloadView = 1;
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public void run() {
        try {
            URL url = new URL(DownloadKey.apkUrl);
            System.out.println("apkUrl" + DownloadKey.apkUrl);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.connect();
            length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            File file = new File(DownloadKey.savePath);
            if (!file.exists()) {
                file.mkdir();
            }
            String apkFile = DownloadKey.saveFileName;
            File ApkFile = new File(apkFile);
            FileOutputStream fos = new FileOutputStream(ApkFile);
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void installApk() {
        File apkfile = new File(DownloadKey.saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        System.out.println("ssssssss");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);


    }
}
