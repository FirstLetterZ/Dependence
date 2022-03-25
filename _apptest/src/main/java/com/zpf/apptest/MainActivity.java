package com.zpf.apptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.apptest.R;
import com.zpf.apptest.task.ITaskGroup;
import com.zpf.apptest.task.TaskCenter;
import com.zpf.apptest.task.TaskResult;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TaskCenter tc = new TaskCenter();
        tc.setTaskGroup(1, new ITaskGroup() {
            @Override
            public TaskResult doJob(Object params) {

                return null;
            }
        });
    }
}
