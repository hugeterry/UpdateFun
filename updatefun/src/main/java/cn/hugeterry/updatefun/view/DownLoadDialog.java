package cn.hugeterry.updatefun.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.R;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.utils.GetAppInfo;


public class DownLoadDialog extends Activity {
    private ImageView close;
    private ProgressBar mProgress;

    private Context mContext = UpdateKey.FROMACTIVITY;

    private SharedPreferences sh_updateurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_dialog);

        DownloadKey.saveFileName = DownloadKey.savePath +
                GetAppInfo.getAppPackageName(UpdateKey.FROMACTIVITY) + ".apk";

        sh_updateurl = this.getSharedPreferences("sh_update", MODE_APPEND);
        DownloadKey.apkUrl = sh_updateurl.getString("sh_update_url", "");

        close = (ImageView) findViewById(R.id.download_dialog_close);
        mProgress = (ProgressBar) findViewById(R.id.progressdialog_p);

        new Download(this, mProgress, UpdateKey.DialogOrNotification).start();

        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DownLoadDialog.this,
                        mContext.getClass());
                setResult(3, intent);
                UpdateKey.TOShowDownloadDialog = 1;
                finish();
                DownloadKey.interceptFlag = true;
            }
        });

    }


}
