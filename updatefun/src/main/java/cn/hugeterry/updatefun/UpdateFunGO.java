package cn.hugeterry.updatefun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import cn.hugeterry.updatefun.view.DownLoadDialog;
import cn.hugeterry.updatefun.module.Update;
import cn.hugeterry.updatefun.view.UpdateDialog;
import cn.hugeterry.updatefun.utils.GetAppInfo;


public class UpdateFunGO {

    private Context context;
    private SharedPreferences sh_update;

    private String version = "";

    private String apkUrl = "";

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
            SharedPreferences.Editor up = sh_update.edit();
            up.putString("sh_update_url", update.up_url);
            up.putString("sh_update_changelog", update.changelog);
            up.commit();

            System.out.println("apkUrl: " + apkUrl);
            if (update.version == null) {
                System.out.println("无联网，不更新");
                msg.arg1 = 2;
                up_handler.sendMessage(msg);
            } else if (!update.version.equals(version)) {
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

    public UpdateFunGO(Context context) {
        this.context = context;
        UpdateKey.FROMACTIVITY = context;
        version = GetAppInfo.getAppVersionName(context);

        if (UpdateKey.TOShowDownloadDialog == 0) {
            Thread thread_update = new Thread(new MyRunnable_update());
            thread_update.start();
        }

    }

    public static void showNoticeDialog(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, UpdateDialog.class);
        ((Activity) context).startActivityForResult(intent, 100);
    }

    public static void showDownloadDialog(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownLoadDialog.class);
        ((Activity) context).startActivityForResult(intent, 0);
    }

    public static void onResume(Context context) {
        if (UpdateKey.TOShowDownloadDialog == 2) {
            showDownloadDialog(context);
        }
    }

}
