package cn.hugeterry.updatefun;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.view.DownLoadDialog;
import cn.hugeterry.updatefun.module.Update;
import cn.hugeterry.updatefun.view.UpdateDialog;
import cn.hugeterry.updatefun.utils.GetAppInfo;


public class UpdateFunGO {

    private Context context;
    private SharedPreferences sh_update;

    private String version = "";
    private String apkUrl = "";

    //通知栏进度条
    private static NotificationManager mNotificationManager = null;
    private static Notification mNotification;

    Handler up_handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    // 弹出提示更新对话框
                    showNoticeDialog(context);
                    break;
                default:
                    break;
            }

        }
    };



    class MyRunnable_update implements Runnable {

        @Override
        public void run() {
            // 检测更新
            Update update = new Update();
            update.start();

            Message msg = new Message();
            try {
                update.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sh_update = context.getSharedPreferences("sh_update", context.MODE_APPEND);

            System.out.println("apkUrl: " + apkUrl);
            if (DownloadKey.version == null) {
                System.out.println("无联网，不更新");
                msg.arg1 = 2;
                up_handler.sendMessage(msg);
            } else if (!DownloadKey.version.equals(version)) {
                System.out.println("需更新版本");
                msg.arg1 = 1;
                up_handler.sendMessage(msg);
            } else {
                System.out.println("版本已是最新");
                msg.arg1 = 2;
                up_handler.sendMessage(msg);
            }
        }

    }

    private static volatile UpdateFunGO sInst = null;

    //this is getInstance()
    public static UpdateFunGO init(Context context) {
        UpdateFunGO inst = sInst;
        if (inst == null) {
            synchronized (UpdateFunGO.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new UpdateFunGO(context);
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    private UpdateFunGO(Context context) {
        this.context = context;
        UpdateKey.FROMACTIVITY = context;
        DownloadKey.saveFileName = DownloadKey.savePath +
                GetAppInfo.getAppPackageName(context) + ".apk";
        version = GetAppInfo.getAppVersionName(context);

        if (UpdateKey.TOShowDownloadView == 0) {
            Thread thread_update = new Thread(new MyRunnable_update());
            thread_update.start();
        }
       Thread download= new Download(context, mNotification, mNotificationManager);

    }

    public void showNoticeDialog(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, UpdateDialog.class);
        ((Activity) context).startActivityForResult(intent, 100);
    }

    public static void showDownloadView(Context context) {

        if (UpdateKey.DialogOrNotification == 1) {
            Intent intent = new Intent();
            intent.setClass(context, DownLoadDialog.class);
            ((Activity) context).startActivityForResult(intent, 0);
        } else if (UpdateKey.DialogOrNotification == 2) {

            notificationInit(context);
            new Download(context, mNotification, mNotificationManager).start();
        }

    }

    private static void notificationInit(Context context) {
        //通知栏内显示下载进度条
        Intent intent = new Intent(context, context.getClass());//点击进度条，进入程序
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.icon= R.drawable.ic_close_white_24dp;
        mNotification.tickerText = "开始下载";
        mNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.download_notification_layout);//通知栏中进度布局
        mNotification.contentIntent = pIntent;

    }

    public static void onResume(Context context) {
        if (UpdateKey.TOShowDownloadView == 2) {
            showDownloadView(context);
        }
    }

    public static void onStop(Context context) {

    }



}
