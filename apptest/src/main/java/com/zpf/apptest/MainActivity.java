package com.zpf.apptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.apptest.R;
import com.zpf.tool.fragment.AppFragmentManager;
import com.zpf.tool.fragment.IViewCreator;
import com.zpf.views.tagtext.TagItemClickListener;
import com.zpf.views.tagtext.TagTextView;

public class MainActivity extends AppCompatActivity {
    TagTextView tagTextView;
    private AppFragmentManager viewManager = new AppFragmentManager(getFragmentManager(), new IViewCreator<String, Fragment>() {
        @Override
        public Fragment create(String key) {
            if ("one".equals(key)) {
                return new FragmentOne();
            } else if ("two".equals(key)) {
                return new FragmentTwo();
            } else if ("three".equals(key)) {
                return new FragmentThree();
            } else if ("four".equals(key)) {
                return new FragmentFour();
            }
            return null;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagTextView = findViewById(R.id.tv_tag_text);
        setTagInfo(tagTextView);
        tagTextView.setTagItemClickListener(new TagItemClickListener() {
            @Override
            public boolean onClickItem(int id) {
                Toast.makeText(MainActivity.this, "onClickItem==>" + id, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onClickEllipsis() {
                Toast.makeText(MainActivity.this, "onClickEllipsis", Toast.LENGTH_SHORT).show();
            }
        });
        tagTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "OnLongClickListener==>", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "OnClickListener==>", Toast.LENGTH_SHORT).show();
            }
        });
        viewManager.add(R.id.fl_top, "one")
                .add(R.id.fl_bottom, "two")
                .add(R.id.fl_bottom, "three")
                .add(R.id.fl_bottom, "four")
                .commit();
        viewManager.show("one");
        viewManager.show("four");
    }



    public void clickOne(View view) {
        tagTextView.setContentText("纽约州自5月下旬开始分地区、分阶段重启，之后疫情并未出现大幅反弹。纽约本地媒体PIX11称，尽管单日确诊病例数再次破千，但纽约州疫情仍好于美国许多地区。有卫生专家指出，这次单日新增病例破千，与近期单日检测量大幅增加有关。以6月5日为例，纽约州共有近7.8万人接受检测，1108人确诊，阳性率约1.42%。6月5日以前，单日检测量更少，但确诊人数往往更多。\n" +
                "\n", 0).color = Color.DKGRAY;
        tagTextView.checkRefresh();
    }

    public void clickTwo(View view) {
        viewManager.show("three");
    }

    public void setTagInfo(TagTextView textView) {
        textView.addTextItem("中新社纽约9月26日电 纽约州州长安德鲁·科莫26日称，纽约州25日近10万人接受新冠病毒检测，新增确诊病例1005人，阳性率约1%。", 0).color = Color.BLUE;
        textView.addTextItem("这是纽约州自今年6月5日以来，单日新增病例数首次反弹至千人以上。\n", 1).color = Color.RED;
        textView.addTextItem("科莫当天通过社交媒体和纽约州政府网站更新了疫情相关数据。", 2).color = Color.GREEN;
        textView.addTextItem("25日，纽约州住院治疗的新冠肺炎患者527人，重症监护人数164人，死亡4人。\n纽约州确诊人数累计近45.5万人，死亡人数累计超过2.5万人。", 3).color = Color.YELLOW;
        textView.addTextItem("美联社说，过去几周以来，纽约州单日新增病例数一直在以微小幅度上升。", 4).color = Color.YELLOW;
        textView.addTextItem("这可能与生产经营活动增加，大、中、小学逐步开学等有关。\n", 5).color = Color.RED;
        textView.addTextItem("纽约市公立学校低年级21日起分批次开学；29日起，公校的中高年级也将开始分批次开学。", 6).color = Color.GREEN;
    }

}
