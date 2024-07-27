package com.zpf.aaa.videotool;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzhuoren on 2021/4/30
 * Describe 视频裁剪类
 */
public class VideoClipper implements Runnable {

    private final static String TAG = "VideoClipper";
    private final int MSG_SUCCESSFUL = 0;
    private final int MSG_FAILED = 1;

    private MediaMuxer mMediaMuxer;
    private MediaExtractor mMediaExtractor;
    private VideoCallback mVideoCallback;

    private int mVideoTrackIndex;
    private int mAudioTrackIndex;

    private boolean isCutting = false;

    private String mInput;
    private String mOutput;

    private long mStartTime;
    private long mEndTime;

    private ExecutorService mExecutorService;
    private  Handler UIHandler;

    public VideoClipper() {
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        UIHandler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case MSG_SUCCESSFUL:
                    isCutting = false;
                    if (mVideoCallback != null) {
                        mVideoCallback.onSuccessful(mOutput);
                    }
                    break;
                case MSG_FAILED:
                    isCutting = false;
                    if (mVideoCallback != null) {
                        Exception e = (Exception) msg.obj;
                        mVideoCallback.onFailed(e);
                    }
                    break;
            }
            return false;
        });
    }

    /**
     *
     * @param input 输入MP4
     * @param output 输出MP4
     * @param startTime 开始时间 单位秒
     * @param endTime 结束时间 单位秒
     * @return
     */
    public VideoClipper setPathAndDuration(String input, String output, int startTime, int endTime) {
        this.mInput = input;
        this.mOutput = output;
        this.mStartTime = (long)startTime * 1000 * 1000;
        this.mEndTime = (long)endTime * 1000 * 1000;
        return this;
    }

    public VideoClipper setCallback(VideoCallback videoMixerCallback) {
        this.mVideoCallback = videoMixerCallback;
        return this;
    }

    public void start() {
        if (TextUtils.isEmpty(mOutput)) {
            throw new RuntimeException("output path is null!");
        }
        if (TextUtils.isEmpty(mInput)) {
            throw new RuntimeException("input path is null!");
        }
        if (isCutting) {
            Log.d(TAG, "is cutting,please wait!");
        } else {
            isCutting = true;
            mExecutorService.execute(this);
        }
    }


    private void clip() {
        Log.d(TAG, "clip: input = "+mInput);
        Log.d(TAG, "clip: output = "+mOutput);
        Log.d(TAG, "clip: startTime = "+mStartTime);
        Log.d(TAG, "clip: endTime = "+mEndTime);

        try {


            mMediaMuxer = new MediaMuxer(mOutput, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(mInput);


            mVideoTrackIndex = getTrackIndex(mMediaExtractor, true);
            mAudioTrackIndex = getTrackIndex(mMediaExtractor, false);

            mMediaMuxer.addTrack(mMediaExtractor.getTrackFormat(mVideoTrackIndex));
            mMediaMuxer.addTrack(mMediaExtractor.getTrackFormat(mAudioTrackIndex));


            long duration = mMediaExtractor.getTrackFormat(mVideoTrackIndex).getLong(MediaFormat.KEY_DURATION);
            if(duration<mEndTime){
                throw new RuntimeException("The end time must be less than the duration");
            }

            mMediaMuxer.start();

            mMediaExtractor.seekTo(mStartTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            mMediaExtractor.selectTrack(mVideoTrackIndex);

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            while (true) {
                long sampleTime = mMediaExtractor.getSampleTime();

                if (sampleTime < mStartTime) {
                    mMediaExtractor.advance();
                    continue;
                } else if (sampleTime > mEndTime) {
                    break;
                }

                bufferInfo.size = mMediaExtractor.readSampleData(byteBuffer, 0);
                long pts = mMediaExtractor.getSampleTime() - mStartTime;
                Log.d(TAG, "pts: " + pts);
                bufferInfo.presentationTimeUs = pts;
                bufferInfo.flags = mMediaExtractor.getSampleFlags();
                mMediaMuxer.writeSampleData(mVideoTrackIndex, byteBuffer, bufferInfo);
                mMediaExtractor.advance();
                byteBuffer.clear();
            }


            mMediaExtractor.unselectTrack(mVideoTrackIndex);
            mMediaExtractor.selectTrack(mAudioTrackIndex);

            byteBuffer = ByteBuffer.allocate(500 * 1024);
            bufferInfo = new MediaCodec.BufferInfo();
            while (true) {
                long sampleTime = mMediaExtractor.getSampleTime();

                if (sampleTime < mStartTime) {
                    mMediaExtractor.advance();
                    continue;
                } else if (sampleTime > mEndTime) {
                    break;
                }
                bufferInfo.size = mMediaExtractor.readSampleData(byteBuffer, 0);
                long pts = mMediaExtractor.getSampleTime() - mStartTime;
                Log.d(TAG, "pts: " + pts);
                bufferInfo.presentationTimeUs = pts;
                bufferInfo.flags = mMediaExtractor.getSampleFlags();
                mMediaMuxer.writeSampleData(mAudioTrackIndex, byteBuffer, bufferInfo);
                mMediaExtractor.advance();
                byteBuffer.clear();
            }


            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;

            mMediaExtractor.release();
            mMediaExtractor = null;

            UIHandler.sendEmptyMessage(MSG_SUCCESSFUL);
            Log.d(TAG, "clip: finish");


        } catch (Exception e) {
            Message msg = Message.obtain();
            msg.what = MSG_FAILED;
            msg.obj = e;
            UIHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }

    private int getTrackIndex(MediaExtractor mediaExtractor, boolean isVideo) {
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
            String mime = trackFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(isVideo ? "video/" : "audio/")) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        clip();
    }
}
