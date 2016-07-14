package cn.hugeterry.updatefun;

import android.content.Context;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/2/22 03:20
 */
public class Key {
    public static String API_TOKEN = "";
    public static String RELEASEID = "";
    public static String UPDATEURL = "http://api.fir.im/apps/latest/" + RELEASEID + "?api_token=" + API_TOKEN;

    public static Context FROMACTIVITY = null;
    public static int TOShowDownloadDialog = 0;
}
