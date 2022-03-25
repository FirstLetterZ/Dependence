package com.zpf.apptest.tst;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Created by ZPF on 2021/11/18.
 */
public class PageLayoutAnimator {
    private final LayoutTransition transition;
    private final Animator leftIn;
    private final Animator rightOut;

    public PageLayoutAnimator(Context context) {
        transition = new LayoutTransition();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int shortSide = Math.min(dm.widthPixels, dm.heightPixels);
        AnimatorSet as1 = new AnimatorSet();
        Animator tx1 = ObjectAnimator.ofFloat(null, "translationX", shortSide, 0f);
        Animator aa1 = ObjectAnimator.ofFloat(null, "Alpha", 0f, 1f);
        as1.playTogether(tx1, aa1);
        leftIn = as1;

        AnimatorSet as2 = new AnimatorSet();
        Animator tx2 = ObjectAnimator.ofFloat(null, "translationX", 0f, shortSide);
        Animator aa2 = ObjectAnimator.ofFloat(null, "Alpha", 1f, 0f);
        as2.playTogether(tx2, aa2);
        rightOut = as2;

        // View 页面转场动画
        transition.setAnimator(LayoutTransition.APPEARING, leftIn);
        transition.setAnimator(LayoutTransition.DISAPPEARING, rightOut);
        // 动画执行时间
        transition.setDuration(300);
        transition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                disable();
            }
        });
    }

    public void enable() {
        transition.enableTransitionType(LayoutTransition.APPEARING);
        transition.enableTransitionType(LayoutTransition.DISAPPEARING);
        transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    public void disable() {
        transition.disableTransitionType(LayoutTransition.APPEARING);
        transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        transition.disableTransitionType(LayoutTransition.CHANGE_APPEARING);
        transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        transition.disableTransitionType(LayoutTransition.CHANGING);
    }

}
