package cn.hugeterry.updatefun.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.hugeterry.updatefun.Key;
import cn.hugeterry.updatefun.R;


public class UpdateDialog extends Activity {
    TextView yes, no;
    TextView tv, tv_changelog;
    SharedPreferences sh_changelog;
    String changelog;
    Context context = Key.FROMACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatedialog);

        yes = (TextView) findViewById(R.id.updatedialog_yes);
        no = (TextView) findViewById(R.id.updatedialog_no);
        tv = (TextView) findViewById(R.id.updatedialog_text);
        tv_changelog = (TextView) findViewById(R.id.updatedialog_text_changelog);

        sh_changelog = this.getSharedPreferences("sh_update", MODE_APPEND);
        changelog = sh_changelog.getString("sh_update_changelog", "");
        tv_changelog.setText("更新日志：\n" + changelog);

        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialog.this, context.getClass());
                setResult(2, intent);
                finish();

            }
        });

        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialog.this, context.getClass());
                setResult(1, intent);
                finish();
            }
        });
    }


}
