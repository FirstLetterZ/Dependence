package com.zpf.aaa.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TestConfigLayout extends ListView {

    public TestConfigLayout(Context context) {
        super(context);
    }
    public TestConfigLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public TestConfigLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public TestConfigLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //键值对存储

    public void add(SaveConfig info) {
        //创建视图，
        //绑定数据
        //设置点击事件

    }

    public void callback(String key, String value) {
        //更新数据
        //更新视图
        //保存
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    public void onClickButton() {

    }

    public void onLongClickContent() {

    }

    public interface ListListenr {
        void onBind(ItemHolder holder, Pair<String, String> data, int position);

        void onClickButton(ItemHolder holder, Pair<String, String> data, int position);

        void onLongClickContent(ItemHolder holder, Pair<String, String> data, int position);
    }



    private class ListAdapter<D,H> extends BaseAdapter {

        protected final ArrayList<Pair<String, String>> dataList = new ArrayList<>();


        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Pair<String, String> getItem(int position) {
            if (position < 0 || position > dataList.size() - 1) {
                return null;
            }
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;
            if (convertView != null) {
                Object obj = convertView.getTag();
                if (obj instanceof ItemHolder) {
                    holder = (ItemHolder) obj;
                }
            }
            final Pair<String, String> itemData = getItem(position);
            if (holder == null) {
                holder = new ItemHolder(parent);
                holder.btnOption.setOnClickListener(v -> {

                });
                GradientDrawable contentBg = new GradientDrawable();
                contentBg.setCornerRadius(10f);
                contentBg.setColor(Color.LTGRAY);
                holder.tvContent.setOnLongClickListener(v -> {
                    //todo zpf 复制内容到剪切板
                    return false;
                });
            }

            return holder.root;
        }
    }

    public static class ItemHolder {
        public final View root;
        public final TextView tvTitle;
        public final TextView tvContent;
        public final Button btnOption;

        public ItemHolder(ViewGroup parent) {
            root = LayoutInflater.from(parent.getContext()).inflate(com.zpf.views.R.layout.views_item_config_option, parent, false);
            tvTitle = root.findViewById(com.zpf.views.R.id.tv_title);
            tvContent = root.findViewById(com.zpf.views.R.id.tv_content);
            btnOption = root.findViewById(com.zpf.views.R.id.btn_option);
            root.setTag(this);
        }
    }
}
