package com.zpf.aaa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(v -> {
            startActivity(new Intent(this, VideoActivity.class));
//            startActivity(new Intent(this, TestActivity.class));
        });
    }

}
