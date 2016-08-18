package cn.hugeterry.updatefun.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import cn.hugeterry.updatefun.config.DownloadKey;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/18 16:52
 */
public class InstallApk {
    public static void startInstall(Context context) {
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
