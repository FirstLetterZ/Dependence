package com.zpf.rvexpand;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreHelper {
    private long lastScrollBottomTime;
    private boolean enable = true;
    private boolean loading = false;
    private RecyclerView.ViewHolder bottomViewHolder;
    private BottomHolderListener listener;
    private RecyclerView.OnScrollListener scrollEndListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            final View itemView = bottomViewHolder.itemView;
            if (!enable) {
                itemView.setVisibility(View.GONE);
                return;
            }
            if (listener == null || loading || System.currentTimeMillis() - lastScrollBottomTime < 500) {
                return;
            }
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                if (recyclerView.getChildAt(i) == itemView) {
                    lastScrollBottomTime = System.currentTimeMillis();
                    changeState(listener.shouldShowHolder(bottomViewHolder), true);
                    break;
                }
            }
        }
    };

    public LoadMoreHelper(@NonNull RecyclerView.ViewHolder bottomViewHolder) {
        this.bottomViewHolder = bottomViewHolder;
    }

    public void attachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(scrollEndListener);
    }

    public void detachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(scrollEndListener);
    }

    public void changeState(boolean loadMore, boolean enableLoad) {
        loading = loadMore;
        enable = enableLoad;
        final View itemView = bottomViewHolder.itemView;
        if (!enableLoad) {
            itemView.setVisibility(View.GONE);
        } else if (loading) {
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
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
    public RecyclerView.ViewHolder getBottomHolder() {
        return bottomViewHolder;
    }

}
