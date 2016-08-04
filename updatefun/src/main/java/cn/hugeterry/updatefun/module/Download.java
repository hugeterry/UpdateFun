package cn.hugeterry.updatefun.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
import android.widget.TextView;

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
    private NotificationManager notificationManager = null;

    private Context context;
    private ProgressBar progressBar;
    private TextView textView;

    private int length;
    private int count;


    public Download(Context context, ProgressBar progressBar, TextView textView) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
    }

    public Download(Context context, Notification.Builder builder, NotificationManager notificationManager) {
        this.context = context;
        this.builder = builder;
        this.notificationManager = notificationManager;
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (UpdateKey.DialogOrNotification == 1) {
                        progressBar.setProgress(progress);
                        textView.setText(progress + "%");
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setProgress(length, count, false)
                                .setContentText("下载进度:" + progress + "%");
                        notificationManager.notify(1115, builder.build());
                    }
                    break;
                case DOWN_OVER:
                    if (UpdateKey.DialogOrNotification == 1) {
                        ((Activity) context).finish();
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        builder.setTicker("下载完成");
                        notificationManager.notify(1115, builder.build());
                        notificationManager.cancel(1115);
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
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            url = new URL(DownloadKey.apkUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("apkUrl" + DownloadKey.apkUrl);
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
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }


    private void installApk() {
        File apkfile = new File(DownloadKey.saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);


    }
}
