package com.zpf.apptest;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptest.R;
import com.zpf.views.TriangleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
    private int d = 0;
    TriangleView triangle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        triangle = findViewById(R.id.triangle);
        float d = getResources().getDisplayMetrics().density;
//        triangle.setCornerRadius(5 * d, 10 * d);

    }

    public void clickOne(View view) {

    }
    public void clickTwo(View view) {
        int d = (triangle.getDirection() + 1) % 4;
        triangle.setDirection(d);
        triangle.invalidate();
    }
}