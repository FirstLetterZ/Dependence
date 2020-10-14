package com.zpf.apptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zpf.views.FoldedTagTextView;
import com.zpf.views.TypeClickListener;
import com.zpf.views.TypeTextInfo;
import com.zpf.views.tagtext.TagTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TagTextView tagTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTypeText((FoldedTagTextView) findViewById(R.id.tv_tag));
        tagTextView = findViewById(R.id.tv_tag_text);
        setTagInfo(tagTextView);
//        tagTextView.setTypeClickListener(new TypeClickListener() {
//            @Override
//            public boolean onClickTypeText(int type, int id) {
//                Toast.makeText(MainActivity.this, "id=" + id, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public void onClickEllipsis() {
//                Toast.makeText(MainActivity.this, "onClickEllipsis", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void clickOne(View view) {
        tagTextView.setContentText("纽约州自5月下旬开始分地区、分阶段重启，之后疫情并未出现大幅反弹。纽约本地媒体PIX11称，尽管单日确诊病例数再次破千，但纽约州疫情仍好于美国许多地区。有卫生专家指出，这次单日新增病例破千，与近期单日检测量大幅增加有关。以6月5日为例，纽约州共有近7.8万人接受检测，1108人确诊，阳性率约1.42%。6月5日以前，单日检测量更少，但确诊人数往往更多。\n" +
                "\n", 0).color = Color.DKGRAY;
        tagTextView.checkRefresh();
    }

    public void clickTwo(View view) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void setTagInfo(TagTextView textView) {
        textView.addTextItem("中新社纽约9月26日电 纽约州州长安德鲁·科莫26日称，纽约州25日近10万人接受新冠病毒检测，新增确诊病例1005人，阳性率约1%。", 0).color = Color.BLUE;
        textView.addTextItem("这是纽约州自今年6月5日以来，单日新增病例数首次反弹至千人以上。\n", 1).color = Color.RED;
        textView.addTextItem("科莫当天通过社交媒体和纽约州政府网站更新了疫情相关数据。", 2).color = Color.GREEN;
        textView.addTextItem("25日，纽约州住院治疗的新冠肺炎患者527人，重症监护人数164人，死亡4人。\n纽约州确诊人数累计近45.5万人，死亡人数累计超过2.5万人。", 3).color = Color.YELLOW;
        textView.addTextItem("美联社说，过去几周以来，纽约州单日新增病例数一直在以微小幅度上升。", 0).color = Color.YELLOW;
        textView.addTextItem("这可能与生产经营活动增加，大、中、小学逐步开学等有关。\n", 1).color = Color.RED;
        textView.addTextItem("纽约市公立学校低年级21日起分批次开学；29日起，公校的中高年级也将开始分批次开学。", 2).color = Color.GREEN;
    }

    public void setTypeText(FoldedTagTextView tagTextView) {
        final List<TypeTextInfo> textInfoList = new ArrayList<>();

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
        info6.id = 5;
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
