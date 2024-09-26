package com.zpf.aaa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        RecyclerView rvList = findViewById(R.id.rv_list);
        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setJustifyContent(JustifyContent.CENTER);
        manager.setAlignItems(AlignItems.CENTER);
        List<String> list = new ArrayList<>();
        TestAdapter adapter = new TestAdapter(list);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);

        Random random = new Random();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                list.add("努力可能了sadtgshgare韩国克里斯朵夫好看；速；两个人的时候；hi刚萨；客户；1较高的；法律后果了；等候；i努力可能了:Test=" + random.nextInt());
                adapter.notifyDataSetChanged();
                if (list.size() < 20) {
                    rvList.postDelayed(this, 1000L);
                }
            }
        };
        rvList.postDelayed(runnable1, 1000L);

    }

    private static class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
        private final List<String> dataList;
        public TestAdapter(List<String> dataList) {
            this.dataList = dataList;
        }
        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false);
            return new TestViewHolder(textView);
        }
        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            String str = dataList.get(position);
            ((TextView) holder.itemView).setText(str);
        }
        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
