package com.zpf.tool;

import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZPF on 2018/6/15.
 */
public class TimeCountUtil {
    private TimeCountListener listener;
    private CountDownTimer countDownTimer;
    private long offset = 0;

    public TimeCountUtil(long second) {
        createCountDownTimer(second * 1000, 1000);
    }

    public TimeCountUtil(long millisInFuture, long countDownInterval, TimeUnit timeUnit) {
        long unit = timeUnit.toMillis(1);
        createCountDownTimer(millisInFuture * unit, countDownInterval * unit);
    }

    public void setSecondOffset(long offset) {
        this.offset = offset * TimeUnit.SECONDS.toMillis(1);
    }

    public void setUnitOffset(long offset, TimeUnit timeUnit) {
        this.offset = offset * timeUnit.toMillis(1);
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

    private void createCountDownTimer(long millisInFuture, long countDownInterval) {
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long leftTime = millisUntilFinished - offset;
                if (leftTime <= 0) {
                    cancel();
                    if (listener != null) {
                        listener.onTimeFinish();
                    }
                } else if (listener != null) {
                    leftTime = leftTime / 1000;
                    if (listener instanceof SecondCountListener) {
                        ((SecondCountListener) listener).onTimeCutDown(leftTime);
                    } else if (listener instanceof DayCountListener) {
                        int second = (int) (leftTime % 60);
                        leftTime = leftTime / 60;
                        int minute = (int) (leftTime % 60);
                        leftTime = leftTime / 60;
                        int hour = (int) (leftTime % 24);
                        int day = (int) (leftTime / 24);
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
