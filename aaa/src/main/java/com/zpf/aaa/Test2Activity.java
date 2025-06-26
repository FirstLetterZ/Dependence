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

import com.zpf.aaa.banner.CarouselLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        RecyclerView rvList = findViewById(R.id.rv_list);
//        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
//        manager.setFlexDirection(FlexDirection.ROW);
//        manager.setFlexWrap(FlexWrap.WRAP);
//        manager.setJustifyContent(JustifyContent.CENTER);
//        manager.setAlignItems(AlignItems.CENTER);

//        RepeatLayoutManager manager = new RepeatLayoutManager(RecyclerView.HORIZONTAL);
        float d = getResources().getDisplayMetrics().density;
        CarouselLayoutManager manager = new CarouselLayoutManager();
        int size = (int) (120f * d);
        int space = (int) (10f * d);
        manager.setItemSize(size, size);
        manager.setItemSpace(-space);
        manager.setOrientation(RecyclerView.HORIZONTAL);
//        CarouseLayoutManager manager = new CarouseLayoutManager();
//        manager.setOrientation(RecyclerView.VERTICAL);
//        manager.setOrientation(RecyclerView.HORIZONTAL);
//        PagerSnapHelper snapHelper = new PagerSnapHelper();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("text:\n" + i + "\n" + i + "\n" + i);
        }
        TestAdapter adapter = new TestAdapter(list);
//        snapHelper.attachToRecyclerView(rvList);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);

//        Random random = new Random();
//        Runnable runnable1 = new Runnable() {
//            @Override
//            public void run() {
//                list.add("努力可能了sadtgshgare韩国克里斯朵夫好看；速；两个人的时候；hi刚萨；客户；1较高的；法律后果了；等候；i努力可能了:Test=" + random.nextInt());
//                adapter.notifyDataSetChanged();
//                if (list.size() < 20) {
//                    rvList.postDelayed(this, 1000L);
//                }
//            }
//        };
//        rvList.postDelayed(runnable1, 1000L);
        rvList.postDelayed(new Runnable() {
            @Override
            public void run() {
//                manager.smoothScrollToPosition();
                rvList.smoothScrollToPosition(6);
            }
        }, 3000L);
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
