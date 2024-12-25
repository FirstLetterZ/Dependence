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
