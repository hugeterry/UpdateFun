package cn.hugeterry.updatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.hugeterry.updatefun.Key;
import cn.hugeterry.updatefun.UpdateFunGO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Key.API_TOKEN = "";
        Key.RELEASEID = "";
        new UpdateFunGO(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }
}
