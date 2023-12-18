package com.zpf.apptest;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apptest.R;
import com.zpf.apptest.tst.RoundLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RoundLayout roundLayout = findViewById(R.id.rl_view);
        roundLayout.setDrawCircle(true);
        float r = 8 * getResources().getDisplayMetrics().density;
//        roundLayout.setConnerRadius(r);


    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnim();
    }

    public void startAnim() {
        TypeEvaluator<Pair<Float, Float>> typeEvaluator = new TypeEvaluator<Pair<Float, Float>>() {

            @Override
            public Pair<Float, Float> evaluate(float fraction, Pair<Float, Float> startValue, Pair<Float, Float> endValue) {
                Log.e("ZPF", "evaluate==>fraction=" + fraction + ";startValue=" + startValue.toString() + ";endValue=" + endValue.toString());
                return startValue;
            }
        };
        ValueAnimator animator = ObjectAnimator.ofObject(typeEvaluator, new Pair<Float, Float>(1.0f, 1.0f), new Pair<Float, Float>(2.0f, 2.0f), new Pair<Float, Float>(3.0f, 3.0f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                Log.e("ZPF", "onAnimationUpdate==>fraction=" + animation.getAnimatedFraction() + ";value=" + animation.getAnimatedValue().toString());
                animation.getAnimatedValue();
            }
        });
        animator.setDuration(500);
        animator.start();
    }
}
