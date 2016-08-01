package cn.hugeterry.updatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.UpdateFunGO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的RELEASE_ID
        UpdateKey.API_TOKEN = "";
        UpdateKey.RELEASE_ID = "573ecefa748aac36f4000007";
        //如果你想通过Dialog来进行下载，可以如下设置
        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
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
