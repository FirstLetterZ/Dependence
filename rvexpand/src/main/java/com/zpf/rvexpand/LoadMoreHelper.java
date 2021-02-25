package com.zpf.rvexpand;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreHelper {
    private long lastScrollBottomTime;
    private boolean enable = true;
    private boolean loading = false;
    private final LoadMoreViewHolder viewHolder;
    private BottomHolderListener listener;
    private final RecyclerView.OnScrollListener scrollEndListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (!enable) {
                viewHolder.onLoading(false);
                return;
            }
            if (listener == null || loading || System.currentTimeMillis() - lastScrollBottomTime < 500) {
                return;
            }
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                if (recyclerView.getChildAt(i) == viewHolder.getItemView()) {
                    lastScrollBottomTime = System.currentTimeMillis();
                    changeState(listener.shouldShowHolder(viewHolder), true);
                    break;
                }
            }
        }
    };

    public LoadMoreHelper(@NonNull LoadMoreViewHolder holder) {
        this.viewHolder = holder;
        viewHolder.onLoading(false);
    }

    public void attachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(scrollEndListener);
    }

    public void detachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(scrollEndListener);
    }

    public void changeState(boolean loadMore, boolean enableLoad) {
        if (enable != enableLoad) {
            enable = enableLoad;
            if (!enable) {
                loadMore = false;
            }
        }
        if (loading != loadMore) {
            loading = loadMore;
            viewHolder.onLoading(loading);
        }
    }

    public void stopLoad() {
        changeState(false, enable);
    }

    public void finishLoad(boolean enableLoadMore) {
        changeState(false, enableLoadMore);
    }

    public void setBottomHolderListener(BottomHolderListener listener) {
        this.listener = listener;
    }

    @NonNull
    public LoadMoreViewHolder getViewHolder() {
        return viewHolder;
    }

}
