package cn.hugeterry.updatedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.UpdateFunGO;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/14 09：35
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的应用ID
        UpdateKey.API_TOKEN = "";
        UpdateKey.APP_ID = "578a11e7748aac01b7000039";
        //如果你想通过Dialog来进行下载，可以如下设置
//        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
    }

    public void toUpdateView(View view) {
        //跳转至手动更新Activity
        Intent intent = new Intent(this, ManualUpdateActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }
}
