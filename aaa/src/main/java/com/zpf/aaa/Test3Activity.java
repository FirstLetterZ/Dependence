package com.zpf.aaa;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zpf.aaa.view.AiBottomHintView;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class Test3Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        AiBottomHintView view = findViewById(R.id.tv_test);
        view.setMatchSource("有在获取焦点后才会滚动显示");
//        view.setMatchSource("有在获取焦点后才会滚动显示隐藏文字,因此需要在包中新建一个类,继承TextView。");
//        view.setScrollContainer(true);
//        view.setMovementMethod(new ScrollingMovementMethod());
//        view.postDelayed(() -> {
//            view.match("有在此点后");
//        },1000L);
//
//        view.postDelayed(() -> {
//            view.match("才获取焦点");
//        },2000L);
//
//        view.postDelayed(() -> {
//            view.match("点后才会滚动");
//        },3000L);
//        view.postDelayed(() -> {
//            view.match("会滚动显示隐藏");
//        },4000L);
//        view.postDelayed(() -> {
//            view.match("显示隐文");
//        },5000L);
//        view.postDelayed(() -> {
//            view.match("藏文字因此需");
//        },6000L);
//        view.postDelayed(() -> {
//            view.match("因此需要在包中");
//        },7000L);
//        view.postDelayed(() -> {
//            view.match("此需要在包中新建一个");
//        },8000L);
    }
}