package com.zpf.tool;

import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZPF on 2018/6/15.
 */
public class TimeCountUtil {
    private long unit = 1;
    private TimeCountListener listener;
    private CountDownTimer countDownTimer;

    public TimeCountUtil(long second) {
        this.unit = 1000;
        createCountDownTimer(second * unit, unit);
    }

    private TimeCountUtil(long millisInFuture, TimeUnit timeUnit) {
        initUtil(timeUnit);
        createCountDownTimer(millisInFuture * unit, unit);
    }

    public void setListener(TimeCountListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    public void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initUtil(TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.SECONDS) {
            this.unit = 1000;
        } else if (timeUnit == TimeUnit.MINUTES) {
            this.unit = 1000 * 60;
        } else if (timeUnit == TimeUnit.HOURS) {
            this.unit = 1000 * 60 * 60;
        } else if (timeUnit == TimeUnit.DAYS) {
            this.unit = 1000 * 60 * 60 * 24;
        }
    }

    private void createCountDownTimer(long millisInFuture, long countDownInterval) {
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (listener != null) {
                    millisUntilFinished = millisUntilFinished / 1000;
                    if (listener instanceof SecondCountListener) {
                        ((SecondCountListener) listener).onTimeCutDown(millisUntilFinished);
                    } else if (listener instanceof DayCountListener) {
                        int second = (int) (millisUntilFinished % 60);
                        millisUntilFinished = millisUntilFinished / 60;
                        int minute = (int) (millisUntilFinished % 60);
                        millisUntilFinished = millisUntilFinished / 60;
                        int hour = (int) (millisUntilFinished % 24);
                        int day = (int) (millisUntilFinished / 24);
                        ((DayCountListener) listener).onTimeCutDown(day, hour, minute, second);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onTimeFinish();
                }
            }
        };
    }

    public interface SecondCountListener extends TimeCountListener {
        void onTimeCutDown(long second);
    }

    public interface DayCountListener extends TimeCountListener {
        void onTimeCutDown(int day, int hour, int minute, int second);
    }

    public interface TimeCountListener {
        void onTimeFinish();
    }

}
