package com.zpf.aaa;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.zpf.views.stretchy.ScrollLoadLayout;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ScrollLoadLayout pullLoadLayout = findViewById(R.id.pll_load);
        pullLoadLayout.addStateListener((target, left, top, right, bottom) -> {
            if (left == ScrollLoadLayout.STATE_LOADING) {
                pullLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadLayout.finishLoading(0);
                    }
                }, 3000L);
            }
            if (right == ScrollLoadLayout.STATE_LOADING) {
                pullLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadLayout.finishLoading(2);
                    }
                }, 3000L);
            }
        });
        ViewPager2 page = findViewById(R.id.vp_page);
        page.setOffscreenPageLimit(1);
        page.setAdapter(new VideoPageAdapter());
    }

}
