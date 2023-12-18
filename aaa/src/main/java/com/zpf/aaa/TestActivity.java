package com.zpf.aaa;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zpf.views.stretchy.ScrollLoadLayout;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ScrollLoadLayout pullLoadLayout = findViewById(R.id.pll_load);
        pullLoadLayout.addStateListener((target, let, top, right, bottom) -> {
            if(top== ScrollLoadLayout.STATE_LOADING){
                pullLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadLayout.finishLoading(1);
                    }
                },5000L);
            }
            if(bottom== ScrollLoadLayout.STATE_LOADING){
                pullLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadLayout.finishLoading(3);
                    }
                },5000L);
            }
        });
        TextView tvFirst = findViewById(R.id.tv_first);
        TextView tvLast = findViewById(R.id.tv_last);
        tvFirst.setOnClickListener(v -> {
            Log.e("ZPF","OnClick tvFirst");

        });
        tvLast.setOnClickListener(v -> {
            Log.e("ZPF","OnClick tvLast");
        });
    }

}
