package cn.hugeterry.updatefun.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.hugeterry.updatefun.UpdateKey;
import cn.hugeterry.updatefun.R;
import cn.hugeterry.updatefun.utils.GetAppInfo;


public class DownLoadDialog extends Activity {
    ImageView close;
    private Context mContext = UpdateKey.FROMACTIVITY;

    // 返回的安装包url
    private String apkUrl;

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/updateFun/";

    private static String saveFileName = savePath + "newversion.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private SharedPreferences sh_updateurl;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloaddialog);

        saveFileName = savePath +
                GetAppInfo.getAppPackageName(UpdateKey.FROMACTIVITY) + ".apk";

        sh_updateurl = this.getSharedPreferences("sh_update", MODE_APPEND);
        apkUrl = sh_updateurl.getString("sh_update_url", "");

        close = (ImageView) findViewById(R.id.download_dialog_close);
        mProgress = (ProgressBar) findViewById(R.id.progressdialog_p);
        downloadApk();
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DownLoadDialog.this,
                        mContext.getClass());
                setResult(3, intent);
                UpdateKey.TOShowDownloadDialog = 1;
                finish();
                interceptFlag = true;
            }
        });

    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        DownLoadDialog.this.startActivity(i);

    }

}
