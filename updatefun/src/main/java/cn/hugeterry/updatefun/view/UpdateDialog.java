package cn.hugeterry.updatefun.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.R;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/16 19:25
 */
public class UpdateDialog extends Activity {

    private TextView yes, no;
    private TextView tv_changelog;

    private Context context = DownloadKey.FROMACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog);

        yes = (TextView) findViewById(R.id.updatedialog_yes);
        no = (TextView) findViewById(R.id.updatedialog_no);
        tv_changelog = (TextView) findViewById(R.id.updatedialog_text_changelog);

        tv_changelog.setText("更新日志：\n" + DownloadKey.changeLog);

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialog.this, context.getClass());
                setResult(2, intent);
                DownloadKey.TOShowDownloadView = 2;
                finish();
            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialog.this, context.getClass());
                setResult(1, intent);
                DownloadKey.TOShowDownloadView = 1;
                if (DownloadKey.ISManual) {
                    DownloadKey.LoadManual = false;
                }
                finish();
            }
        });
    }


}
