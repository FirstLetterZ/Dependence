package com.zpf.aaa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

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
        findViewById(R.id.btn_test1).setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
        });
        findViewById(R.id.btn_test2).setOnClickListener(v -> {
            startActivity(new Intent(this, Test2Activity.class));
        });
        findViewById(R.id.btn_test3).setOnClickListener(v -> {
            startActivity(new Intent(this, Test3Activity.class));
        });
        findViewById(R.id.btn_test4).setOnClickListener(v -> {
            startActivity(new Intent(this, Test4Activity.class));
        });
        TextView tvTest = findViewById(R.id.tv_test);
        tvTest.setFirstBaselineToTopHeight(20);
        tvTest.setLastBaselineToBottomHeight(20);
        SpannableString ss = new SpannableString("古埃及人曾经考虑关于如下问题：如何将一个分数写成形如1/n的分数之和？即写成那些分子是1，分母是正整数的分数之和");
        ss.setSpan(new ImageSpan(this, R.drawable.ic_camera_white,ImageSpan.ALIGN_BASELINE), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTest.setText(ss);
//        PermissionManager.get().addChecker(Fragment.class, new CompatPermissionChecker());
//        SoundMarkHelper helper=new SoundMarkHelper();
//        helper.addChineseMarkInfo("能Happy一天","néng Happy yì tiān");
//        helper.addChineseMarkInfo("打扫干净","da sao gan jing");
//        Log.e("ZPF", helper.formatChinese("能"));
//        Log.e("ZPF", helper.formatChinese("能Happy一天"));
//        Log.e("ZPF", helper.formatChinese("一天"));
//        Log.e("ZPF", helper.formatChinese("不能Happy一天了吗"));
//        Log.e("ZPF", helper.formatChinese("打—扫—干—净—了吗"));
    }

}
