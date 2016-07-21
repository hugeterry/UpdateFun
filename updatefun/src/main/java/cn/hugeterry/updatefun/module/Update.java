package cn.hugeterry.updatefun.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.hugeterry.updatefun.config.UpdateKey;


public class Update extends Thread {

    public String result;
    public String changelog;
    public String version;
    public String up_url;
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
                changelog = object.getString("changelog");
                version = object.getString("versionShort");
                up_url = object.getString("installUrl");
                System.out.println("4sssssssssssss" + changelog + version + up_url);
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
