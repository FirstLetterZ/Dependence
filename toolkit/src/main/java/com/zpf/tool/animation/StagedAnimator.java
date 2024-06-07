package com.zpf.tool.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StagedAnimator {
    protected final ValueAnimator animator;
    private final List<Float> stageList = new ArrayList<>();
    private int lastStage = -1;
    protected final AtomicInteger state = new AtomicInteger(0);
    protected final HashSet<StagedAnimatorListener> listenerSet = new HashSet<>();

    public StagedAnimator() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float fv = (float) animation.getAnimatedValue();
            int stageIndex = lastStage;
            while (stageIndex < 0 || fv > (stageList.get(stageIndex))) {
                if (stageIndex < stageList.size() - 1) {
                    stageIndex++;
                } else {
                    break;
                }
            }
            float percent;
            if (stageIndex == 0) {
                percent = fv / stageList.get(stageIndex);
            } else {
                percent = (fv - stageList.get(stageIndex - 1)) / (stageList.get(stageIndex) - stageList.get(stageIndex - 1));
            }
            onUpdate(stageIndex, percent, lastStage != stageIndex);
            lastStage = stageIndex;
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                state.set(1);
            }
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                boolean canceled = state.get() < 0;
                state.set(0);
                onComplete(canceled);
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                state.set(-1);
            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    protected void onUpdate(int stage, float percent, boolean stageChanged) {
        if (listenerSet.isEmpty()) {
            return;
        }
        for (StagedAnimatorListener listener : listenerSet) {
            listener.onUpdate(stage, percent, stageChanged);
        }
    }

    protected void onComplete(boolean canceled) {
        if (listenerSet.isEmpty()) {
            return;
        }
        for (StagedAnimatorListener listener : listenerSet) {
            listener.onComplete(canceled);
        }
    }

    public void addListener(StagedAnimatorListener listener) {
        if (listener == null) {
            return;
        }
        listenerSet.add(listener);
    }

    public void removeListener(StagedAnimatorListener listener) {
        if (listener == null) {
            return;
        }
        listenerSet.remove(listener);
    }

    public void clearListeners() {
        listenerSet.clear();
    }

    public void start() {
        animator.start();
    }

    public void cancel() {
        animator.cancel();
    }

    public boolean isStarted() {
        return state.get() > 0;
    }

    public long getDuration() {
        return animator.getDuration();
    }

    public void setDurations(List<Long> durationList) {
        if (isStarted()) {
            animator.cancel();
        }
        if (durationList == null || durationList.isEmpty()) {
            return;
        }
        stageList.clear();
        long totalDuration = 0;
        for (long duration : durationList) {
            totalDuration += duration;
        }
        int size = durationList.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                stageList.add(1f);
            } else if (i > 0) {
                stageList.add(stageList.get(i - 1) + durationList.get(i) * 1f / totalDuration);
            } else {
                stageList.add(durationList.get(i) * 1f / totalDuration);
            }
        }
        animator.setDuration(totalDuration);
    }

    public void setDurations(long[] durationArray) {
        if (isStarted()) {
            animator.cancel();
        }
        if (durationArray == null || durationArray.length == 0) {
            return;
        }
        stageList.clear();
        long totalDuration = 0;
        for (long duration : durationArray) {
            totalDuration += duration;
        }
        int size = durationArray.length;
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                stageList.add(1f);
            } else if (i > 0) {
                stageList.add(stageList.get(i - 1) + durationArray[i] * 1f / totalDuration);
            } else {
                stageList.add(durationArray[i] * 1f / totalDuration);
            }
        }
        animator.setDuration(totalDuration);
    }

    public int size() {
        return stageList.size();
    }
}
