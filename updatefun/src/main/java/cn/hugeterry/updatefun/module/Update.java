package cn.hugeterry.updatefun.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/13 14:38
 */
public class Update extends Thread {

    public String result;
    private String url = "http://api.fir.im/apps/latest/" + UpdateKey.RELEASE_ID
            + "?api_token=" + UpdateKey.API_TOKEN;


    public void run() {
        try {
            URL httpUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) httpUrl
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;

                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                result = new String(sb.toString().getBytes(), "utf-8");
                JSONObject object = new JSONObject(result);
                DownloadKey.changeLog = object.getString("changelog");
                DownloadKey.version = object.getString("versionShort");
                DownloadKey.apkUrl = object.getString("installUrl");
                System.out.println("info:" + DownloadKey.changeLog + DownloadKey.version + DownloadKey.apkUrl);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
