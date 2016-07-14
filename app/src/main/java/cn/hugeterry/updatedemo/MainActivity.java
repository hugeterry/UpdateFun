package cn.hugeterry.updatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.hugeterry.updatefun.UpdateFunGO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UpdateFunGO(this);
    }
}
