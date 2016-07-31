package cn.hugeterry.updatefun;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.view.DownLoadDialog;
import cn.hugeterry.updatefun.module.Update;
import cn.hugeterry.updatefun.view.UpdateDialog;
import cn.hugeterry.updatefun.utils.GetAppInfo;


public class UpdateFunGO {

    private Context context;

    private String version = "";
    private static Thread download;

    private static NotificationManager notificationManager = null;
    private static Notification.Builder builder;

    Handler up_handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
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

    //getInstance()
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
        DownloadKey.FROMACTIVITY = context;
        DownloadKey.saveFileName = DownloadKey.savePath +
                GetAppInfo.getAppPackageName(context) + ".apk";
        version = GetAppInfo.getAppVersionName(context);

        if (DownloadKey.TOShowDownloadView == 0) {
            Thread thread_update = new Thread(new MyRunnable_update());
            thread_update.start();
        }


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
            download = new Download(context, builder, notificationManager);
            download.start();
        }

    }

    private static void notificationInit(Context context) {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);

        builder.setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setTicker("开始下载")
                .setContentTitle(GetAppInfo.getAppName(context))
                .setContentText("正在更新")
                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis());
    }

    public static void onResume(Context context) {
        if (DownloadKey.TOShowDownloadView == 2) {
            showDownloadView(context);
        }
    }

    public static void onStop(Context context) {
        if (UpdateKey.DialogOrNotification == 2) {
            download.interrupt();
        }
    }

}
