package cn.hugeterry.updatefun;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.view.DownLoadDialog;
import cn.hugeterry.updatefun.module.Update;
import cn.hugeterry.updatefun.view.UpdateDialog;
import cn.hugeterry.updatefun.utils.GetAppInfo;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/12 16:47
 */
public class UpdateFunGO {

    private Context context;
    private Up_handler up_handler;
    private String version = "";
    private static Thread download;

    private static NotificationManager notificationManager = null;
    private static Notification.Builder builder;

    static class Up_handler extends Handler {
        WeakReference<Context> mActivityReference;

        Up_handler(Context context) {
            mActivityReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final Context context = mActivityReference.get();
            if (context != null) {
                switch (msg.arg1) {
                    case 1:
                        showNoticeDialog(context);
                        break;
                    default:
                        break;
                }
            }
        }
    }

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
                Log.i("UpdateFun TAG", "获取的应用信息为空，不更新，请确认网络是否畅通或者应用ID及API_TOKEN是否正确");
                msg.arg1 = 2;
                up_handler.sendMessage(msg);
            } else if (!DownloadKey.version.equals(version)) {
                Log.i("UpdateFun TAG", "需更新版本");
                msg.arg1 = 1;
                up_handler.sendMessage(msg);
            } else {
                Log.i("UpdateFun TAG", "版本已是最新");
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
        up_handler = new Up_handler(context);

        if (DownloadKey.TOShowDownloadView == 0) {
            Thread thread_update = new Thread(new MyRunnable_update());
            thread_update.start();
        }

    }

    public static void showNoticeDialog(Context context) {
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
        if (DownloadKey.TOShowDownloadView == 2 && UpdateKey.DialogOrNotification == 2) {
            download.interrupt();
        }
    }

}
