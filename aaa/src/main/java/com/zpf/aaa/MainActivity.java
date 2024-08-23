package com.zpf.aaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zpf.tool.compat.permission.CompatPermissionChecker;
import com.zpf.tool.permission.PermissionManager;
import com.zpf.views.TopDropPopup;

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
            View view = getLayoutInflater().inflate(R.layout.customer_snackbar_top, null, false);
           final TopDropPopup popup = new TopDropPopup(this);

//            popup.setHeight(300);
            popup.setContentView(view);
            View parent=getWindow().getDecorView();
            popup.setWidth(parent.getMeasuredWidth());
//            popup.update();
            popup.showAtLocation(parent, Gravity.TOP,0,0);

//            popup.showAsDropDown(parent,0,-parent.getMeasuredHeight());
//            parent.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    popup.dismiss();
//                }
//            },2000L);
        });
        findViewById(R.id.btn_test4).setOnClickListener(v -> {

        });
        PermissionManager.get().addChecker(Fragment.class, new CompatPermissionChecker());
    }

}
