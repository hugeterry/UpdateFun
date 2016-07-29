package cn.hugeterry.updatefun.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.hugeterry.updatefun.R;
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
    private Notification mNotification;
    private NotificationManager mNotificationManager = null;

    private Context context;
    private ProgressBar progressBar;

    private int length;
    private int count = 0;
    int numread;

    public Download(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;

    }

    public Download(Context context, Notification mNotification, NotificationManager mNotificationManager) {
        this.context = context;
        this.mNotification = mNotification;
        this.mNotificationManager = mNotificationManager;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (UpdateKey.DialogOrNotification == 1) {
                        progressBar.setProgress(progress);
                    } else if (UpdateKey.DialogOrNotification == 2) {
                        System.out.println("ssssssssaa");
                        mNotification.contentView.setTextViewText(R.id.notification_name, progress + "%");
                        mNotification.contentView.setProgressBar(R.id.notification_progressbar, length, count, false);
                        mNotificationManager.notify(0, mNotification);
                    }
                    break;
                case DOWN_OVER:
                    System.out.println("DOWN_OVER");
                    if (UpdateKey.DialogOrNotification == 2) {
                        mNotification.contentView.removeAllViews(R.id.notification_progressbar);
                    }
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

            do {
                numread = is.read(buf);
                System.out.println("ddddddd");
                count += numread;
                progress = (int) (((float) count / length) * 100);
                System.out.println("eeeeeee");
                // 更新进度

                handler.sendEmptyMessage(DOWN_UPDATE);

                System.out.println("numread:" + numread);
                if (numread <= 0) {
                    // 下载完成通知安装
                    System.out.println("update now");
                    installApk();
                    break;
                }
                System.out.println("ccccccc");
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
