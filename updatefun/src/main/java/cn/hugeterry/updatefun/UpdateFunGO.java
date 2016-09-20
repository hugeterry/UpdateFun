package cn.hugeterry.updatefun;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.module.HandleUpdateResult;
import cn.hugeterry.updatefun.view.DownLoadDialog;
import cn.hugeterry.updatefun.utils.GetAppInfo;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/12 16:47
 */
public class UpdateFunGO {

    private static Thread download;
    private static Thread thread_update;

    private static volatile UpdateFunGO sInst = null;

    public static void manualStart(Context context) {
        DownloadKey.ISManual = true;
        if (!DownloadKey.LoadManual) {
            DownloadKey.LoadManual = true;
            new UpdateFunGO(context);
        } else Toast.makeText(context, "正在更新中,请稍等", Toast.LENGTH_LONG).show();
    }

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
        DownloadKey.FROMACTIVITY = context;
        if (DownloadKey.TOShowDownloadView != 2) {
            thread_update = new Thread(new HandleUpdateResult(context));
            thread_update.start();
        }
    }

    public static void showDownloadView(Context context) {
        DownloadKey.saveFileName =
                GetAppInfo.getAppPackageName(context) + ".apk";
        if (UpdateKey.DialogOrNotification == 1) {
            Intent intent = new Intent();
            intent.setClass(context, DownLoadDialog.class);
            ((Activity) context).startActivityForResult(intent, 0);
        } else if (UpdateKey.DialogOrNotification == 2) {
            Notification.Builder builder = notificationInit(context);
            download = new Download(context, builder);
            download.start();
        }
    }

    private static Notification.Builder notificationInit(Context context) {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("开始下载")
                .setContentTitle(GetAppInfo.getAppName(context))
                .setContentText("正在更新")
                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis());
        return builder;
    }

    public static void onResume(Context context) {
        if (DownloadKey.TOShowDownloadView == 2) {
            showDownloadView(context);
        } else {
            if (sInst != null) sInst = null;
        }
    }

    public static void onStop(Context context) {
        if (DownloadKey.TOShowDownloadView == 2 && UpdateKey.DialogOrNotification == 2) {
            download.interrupt();
        }
        if (DownloadKey.FROMACTIVITY != null) {
            DownloadKey.FROMACTIVITY = null;
        }
        if (thread_update != null) {
            thread_update.interrupt();
        }
        if (DownloadKey.ISManual) {
            DownloadKey.ISManual = false;
        }
        if (DownloadKey.LoadManual) {
            DownloadKey.LoadManual = false;
        }
    }

}
