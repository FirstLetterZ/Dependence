package com.zpf.aaa.videotool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    VideoMixer videoMixer;
    VideoClipper videoClipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        checkPermission();
        videoMixer = new VideoMixer();
        videoClipper = new VideoClipper();
    }


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

        }
        return false;
    }

    public void merge(View view) {
        File firstFile = new File(Environment.getExternalStorageDirectory(), "input1.mp4");
        File secondFile = new File(Environment.getExternalStorageDirectory(), "input2.mp4");
        File thirdFile = new File(Environment.getExternalStorageDirectory(), "input.mp4");
        File outputFile = new File(Environment.getExternalStorageDirectory(), "output.mp4");
        videoMixer.setPath(outputFile.getAbsolutePath(),
                firstFile.getAbsolutePath(),
                secondFile.getAbsolutePath(),
                thirdFile.getAbsolutePath())
                .setCallback(new VideoCallback() {
                    @Override
                    public void onSuccessful(String path) {
                        Log.d(TAG, "onSuccessful: " + path);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.d(TAG, "onFailed: " + e.getMessage());
                    }
                })
                .start();
    }

    public void split(View view) {
        File input = new File(Environment.getExternalStorageDirectory(), "output.mp4");
        File output = new File(Environment.getExternalStorageDirectory(), "clip.mp4");

        videoClipper.setPathAndDuration(input.getAbsolutePath(), output.getAbsolutePath(), 10, 30)
                .setCallback(new VideoCallback() {
                    @Override
                    public void onSuccessful(String path) {
                        Log.d(TAG, "onSuccessful: " + path);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.d(TAG, "onFailed: " + e.getMessage());
                    }
                }).start();
    }
}