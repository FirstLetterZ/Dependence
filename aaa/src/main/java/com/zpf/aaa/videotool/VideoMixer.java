package com.zpf.aaa.videotool;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by huzhuoren on 2021/4/30
 * Describe 多个视频合成
 */
public class VideoMixer implements Runnable {

    private static final String TAG = "VideoMixer";

    private final int MSG_SUCCESSFUL = 0;

    private final int MSG_FAILED = 1;

    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;
    private VideoCallback mVideoCallback;

    private boolean isMixing = false;
    private String mOutput;
    private String[] mInputs;

    private int mVideoTrackIndex;
    private int mAudioTrackIndex;
    private long mVideoDuration = 0;

    private ExecutorService mExecutorService;
    private Handler UIHandler;

    public VideoMixer() {
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        UIHandler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case MSG_SUCCESSFUL:
                    isMixing = false;
                    if (mVideoCallback != null) {
                        mVideoCallback.onSuccessful(mOutput);
                    }
                    break;
                case MSG_FAILED:
                    isMixing = false;
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
     * @param output 输出文件
     * @param paths 输入文件数组
     * @return
     */
    public VideoMixer setPath(String output, String... paths) {

        this.mOutput = output;
        this.mInputs = new String[paths.length];
        System.arraycopy(paths, 0, mInputs, 0, paths.length);
        return this;
    }

    public VideoMixer setCallback(VideoCallback videoMixerCallback) {
        this.mVideoCallback = videoMixerCallback;
        return this;
    }

    public void start() {
        if (mOutput == null) {
            throw new RuntimeException("please set output path!");
        }
        if (mInputs == null && mInputs.length < 2) {
            throw new RuntimeException("must inputs > 2!");
        }
        if (isMixing) {
            Log.d(TAG, "is mixing,please wait!");
        } else {
            isMixing = true;
            Log.d(TAG, "start mix...");
            mExecutorService.execute(this);
        }

    }

    private void remixVideo() {
        try {
            mMediaMuxer = new MediaMuxer(mOutput, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(500 * 1024);
            for (int i = 0; i < mInputs.length; i++) {

                mMediaExtractor = new MediaExtractor();
                mMediaExtractor.setDataSource(mInputs[i]);
                mVideoTrackIndex = getTrackIndex(mMediaExtractor, true);
                mAudioTrackIndex = getTrackIndex(mMediaExtractor, false);
                if (i == 0) {
                    mVideoDuration = 0;
                    mMediaMuxer.addTrack(mMediaExtractor.getTrackFormat(mVideoTrackIndex));
                    mMediaMuxer.addTrack(mMediaExtractor.getTrackFormat(mAudioTrackIndex));
                    mMediaMuxer.start();
                }
                writeData(mMediaMuxer, mMediaExtractor, bufferInfo, byteBuffer, mVideoTrackIndex, -1, mVideoDuration);
                writeData(mMediaMuxer, mMediaExtractor, bufferInfo, byteBuffer, mAudioTrackIndex, mVideoTrackIndex, mVideoDuration);
                mVideoDuration += mMediaExtractor.getTrackFormat(mVideoTrackIndex).getLong(MediaFormat.KEY_DURATION);
                mMediaExtractor.release();
            }

            mMediaMuxer.stop();
            mMediaMuxer.release();

            mMediaExtractor = null;
            mMediaMuxer = null;

            UIHandler.sendEmptyMessage(MSG_SUCCESSFUL);
            Log.d(TAG, "mix finish!");

        } catch (Exception e) {
            Message msg = Message.obtain();
            msg.what = MSG_FAILED;
            msg.obj = e;
            UIHandler.sendMessage(msg);
            e.printStackTrace();
        }

    }


    private void writeData(MediaMuxer mediaMuxer, MediaExtractor mediaExtractor,
                           MediaCodec.BufferInfo info,
                           ByteBuffer byteBuffer,
                           int selTrack, int unSelTrack, long offset) {

        if (unSelTrack != -1) mediaExtractor.unselectTrack(unSelTrack);
        mediaExtractor.selectTrack(selTrack);
        while (true) {
            int size = mediaExtractor.readSampleData(byteBuffer, 0);
            if (size < 0) {
                break;
            }
            info.size = size;
            info.presentationTimeUs = mediaExtractor.getSampleTime() + offset;
            info.offset = 0;
            info.flags = mediaExtractor.getSampleFlags();
            mediaMuxer.writeSampleData(selTrack, byteBuffer, info);
            mediaExtractor.advance();
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
        remixVideo();
    }
}
