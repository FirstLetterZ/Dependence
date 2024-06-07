package com.zpf.tool.animation;

public interface StagedAnimatorListener {
    void onUpdate(int stage, float percent, boolean stageChanged);

    void onComplete(boolean canceled);
}