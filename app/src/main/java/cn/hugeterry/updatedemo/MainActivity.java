package cn.hugeterry.updatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.hugeterry.updatefun.UpdateKey;
import cn.hugeterry.updatefun.UpdateFunGO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //在http://fir.im/注册账号后获得的API_TOKEN
        UpdateKey.API_TOKEN = "";
        UpdateKey.RELEASE_ID = "578a11e7748aac01b7000039";
        new UpdateFunGO(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }
}
