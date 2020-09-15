package com.example.apptest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zpf.views.FoldedTagTextView;
import com.zpf.views.TypeClickListener;
import com.zpf.views.TypeTextInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<TypeTextInfo> textInfoList = new ArrayList<>();
        FoldedTagTextView tagTextView = findViewById(R.id.tv_tag);
        TypeTextInfo info1 = new TypeTextInfo();
        info1.content = "#标签#";
        info1.color = Color.BLUE;
        info1.id = 0;
        info1.type = 0;

        TypeTextInfo info2 = new TypeTextInfo();
        info2.content = "数据格式规范数据库恢复健康是否就是个很愧疚";
        info2.color = Color.BLACK;
        info2.id = 1;
        info2.type = 1;

        TypeTextInfo info3 = new TypeTextInfo();
        info3.content = "@树大根深个";
        info3.color = Color.RED;
        info3.id = 2;
        info3.type = 2;

        TypeTextInfo info4 = new TypeTextInfo();
        info4.content = "是否过\n很多很多";
        info4.color = Color.DKGRAY;
        info4.id = 3;
        info4.type = 3;

        TypeTextInfo info5 = new TypeTextInfo();
        info5.content = "过来看刚回来是否过很多很多话撒过的大手大脚飞机开放看过来看刚回来";
        info5.color = Color.GREEN;
        info5.id = 4;
        info5.type = 4;

        TypeTextInfo info6 = new TypeTextInfo();
        info6.content = "@三个地方喝喝";
        info6.color = Color.BLUE;
        info6.id = 4;
        info6.type = 5;

        TypeTextInfo info7 = new TypeTextInfo();
        info7.content = "撒过的大手大脚飞机开放看过来看刚回来";
        info7.color = Color.LTGRAY;
        info7.id = 6;
        info7.type = 6;

        textInfoList.add(info1);
        textInfoList.add(info2);
        textInfoList.add(info3);
        textInfoList.add(info4);
        textInfoList.add(info5);
        textInfoList.add(info6);
        textInfoList.add(info7);
        tagTextView.setEllipsisColor(Color.YELLOW);
        tagTextView.setTypeTextArray(textInfoList);
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "default Click ", Toast.LENGTH_SHORT).show();
            }
        });
        tagTextView.setTypeClickListener(new TypeClickListener() {
            @Override
            public boolean onClickTypeText(int type, int id) {
                if (id >= 0 && id < textInfoList.size()) {
                    Toast.makeText(MainActivity.this, textInfoList.get(id).content, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public void onClickEllipsis() {
                Toast.makeText(MainActivity.this, "onClickEllipsis", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
