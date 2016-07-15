package cn.hugeterry.updatefun.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/15 15:58
 */
public class GetAppInfo {
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

}
