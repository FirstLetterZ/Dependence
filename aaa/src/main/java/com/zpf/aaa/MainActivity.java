package com.zpf.aaa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zpf.tool.compat.permission.CompatPermissionChecker;
import com.zpf.tool.permission.PermissionManager;

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

        });
        PermissionManager.get().addChecker(Fragment.class, new CompatPermissionChecker());
    }

}
