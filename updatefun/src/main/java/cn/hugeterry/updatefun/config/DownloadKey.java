package cn.hugeterry.updatefun.config;

import android.content.Context;

/**
 * Created by hugeterry(http://hugeterry.cn)
 */
public class DownloadKey {
    @Deprecated
    public static Context FROMACTIVITY = null;
    public static Boolean ISManual = false;
    public static Boolean LoadManual = false;
    public static int TOShowDownloadView = 0;
    //    public static final String savePath = Environment.getExternalStorageDirectory() + "/UpdateFun/";
    public static String saveFileName = "newversion.apk";
    public static String apkUrl = "";
    public static String changeLog = "";
    public static String version;
    public static boolean interceptFlag = false;
}
