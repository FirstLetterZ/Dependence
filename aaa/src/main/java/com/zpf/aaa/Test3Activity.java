package com.zpf.aaa;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zpf.views.button.FrameButtonLayout;
import com.zpf.views.button.LinearButtonLayout;
import com.zpf.views.button.RelativeButtonLayout;
import com.zpf.views.tagtext.TagItemClickListener;
import com.zpf.views.tagtext.TagTextStyle;
import com.zpf.views.tagtext.TagTextView;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class Test3Activity extends AppCompatActivity {
    private final int requestMaxLine = 2;
    private int textWidth;
    private String content1 = "活力满满，和皮卡丘一起点燃今天的早晨吧！活力满满，和皮卡丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘！丘一起点燃今天的早晨吧！丘今！一起点燃今天的早晨早晨吧！";
    private String content2 = "活力满满，和皮卡丘一起点燃今天的早晨吧！活力满满，和皮卡丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘一起点燃今天的早晨吧！丘！丘一起点燃今天的早晨吧！丘今！丘一起点燃今天的早晨吧！";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        String content = content1;
        final float density = getResources().getDisplayMetrics().density;
        final int mt = (int) (200 * density);
        final int paddingTop = (int) (16 * density);
        textWidth = (int) (36 * density);
        final View pointer1 = findViewById(R.id.view_pointer1);
        final View pointer2 = findViewById(R.id.view_pointer2);
//        final TextView tvContrast = findViewById(R.id.tv_contrast);
//        tvContrast.setText(content);
        final TagTextView view = findViewById(R.id.ttv_test);
        TagTextStyle ellipsisStyle = view.getEllipsisStyle();
        ellipsisStyle.color = Color.WHITE;
        ellipsisStyle.bold = true;
        view.setEllipsisText("...\u3000展开\u3000\u3000");
        TagTextStyle style1 = view.setContentText(content, 1);
        style1.color = Color.WHITE;
//        TagTextStyle style2 = view.addTextItem("   收起", 2);
//        style2.bold = true;
//        style2.color = Color.WHITE;
        view.setMaxLines(requestMaxLine);
        view.setTagItemClickListener(new TagItemClickListener() {
            @Override
            public boolean onClickItem(int id) {
                if (view.getMaxLines() > requestMaxLine) {
                    view.setMaxLines(requestMaxLine);
                } else {
                    view.setMaxLines(Integer.MAX_VALUE);
                }
//                Log.e("ZPF", "view layout 2222==>" + view.getLeft() + "," + view.getTop() + "," + view.getRight() + "," + view.getBottom());
                view.checkRefresh();
                checkState(view, pointer1, pointer2);
                return true;
            }
            @Override
            public void onClickEllipsis() {
                view.setMaxLines(Integer.MAX_VALUE);
                view.checkRefresh();
                checkState(view, pointer1, pointer2);
//                PointF endPoint = view.getTextEndPoint();
//                Log.e("ZPF", "TextEndPoint==>" + endPoint.toString());
//                Log.e("ZPF", "view layout 11111==>" + view.getLeft() + "," + view.getTop() + "," + view.getRight() + "," + view.getBottom());
//                ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) pointer.getLayoutParams());
//                lp.topMargin = (int) (view.getBottom() - pointer.getMeasuredHeight() - paddingTop);
//                lp.leftMargin = (int) (view.getLeft() + endPoint.x);
//                Log.e("ZPF", "  lp.topMargin ==>" + lp.topMargin + ";pointer.getMeasuredHeight=" + pointer.getMeasuredHeight());
            }
        });

        FrameButtonLayout button1 = findViewById(R.id.fl_button1);
        LinearButtonLayout button2 = findViewById(R.id.ll_button2);
        RelativeButtonLayout button3 = findViewById(R.id.rl_button3);
        button1.setConnerRadius(12 * density);
        button2.setConnerRadius(24 * density);
        button3.setDrawCircle(true);
    }

    private void checkState(TagTextView tagTextView, View pointer1, View pointer2) {
        if (tagTextView.getMaxLines() > requestMaxLine) {
            PointF endPoint = tagTextView.getTextEndPoint();
            if (endPoint.x + textWidth > tagTextView.getMeasuredWidth()) {
                pointer1.setVisibility(View.GONE);
                pointer2.setVisibility(View.VISIBLE);
            } else {
                pointer1.setVisibility(View.VISIBLE);
                pointer1.setPadding((int) endPoint.x, 0, 0, 0);
                pointer2.setVisibility(View.GONE);
            }
        } else {
            pointer1.setVisibility(View.GONE);
            pointer2.setVisibility(View.GONE);
        }
    }

}