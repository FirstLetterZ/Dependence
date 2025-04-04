package com.zpf.aaa.utils;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class MediaMuxerUtils {

    private WeakReference<Context> mContext;

    public MediaMuxerUtils(Context context) {
        mContext = new WeakReference<Context>(context);

    }

    //分离MP4中的视频保存成新的MP4文件
    public void videoMuxer(String videoPath) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        int videoIndex = -1;
        try {
            mediaExtractor.setDataSource(videoPath + "/1.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                    videoIndex = i;
                    break;
                }
            }
            mediaExtractor.selectTrack(videoIndex);
            MediaFormat videoTrackFormat = mediaExtractor.getTrackFormat(videoIndex);

            MediaMuxer mediaMuxer = new MediaMuxer(videoPath + "/2.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            //追踪此信道
            int trackIndex = mediaMuxer.addTrack(videoTrackFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            int frameSize = videoTrackFormat.getInteger(MediaFormat.KEY_FRAME_RATE);

            mediaExtractor.selectTrack(videoIndex);
            bufferInfo.presentationTimeUs = 0;
            while (true) {
                int readSampleData = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleData < 0) {
                    break;
                }

                bufferInfo.size = readSampleData;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += 1000 * 1000 / frameSize;
                //写入帧数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);

                mediaExtractor.advance();

            }

            mediaMuxer.stop();
            mediaExtractor.release();
            mediaMuxer.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分离MP4中的音频保存成新的MP4文件
    public void audioMuxer(String videoPath) {
        int audioIndex = -1;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(videoPath + "/1.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioIndex = i;
                    break;
                }
            }
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex);

            MediaMuxer mediaMuxer = new MediaMuxer(videoPath + "/output_audio.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int audioTrackIndex = mediaMuxer.addTrack(trackFormat);
            mediaMuxer.start();

            mediaExtractor.selectTrack(audioIndex);
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            long audioSampleTime = 0;
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC)
                    mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long firstSampleTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long secondSampleTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                audioSampleTime = Math.abs(secondSampleTime - firstSampleTime);
            }

            while (true) {
                int readSampleData = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleData < 0)
                    break;

                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.size = readSampleData;
                bufferInfo.presentationTimeUs += audioSampleTime;

                mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, bufferInfo);

                mediaExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将1.mp4视频、testfile.mp4音频各自分离出来然后合成新的mp4
    public void videoAudioMuxer(String videoPath) {
        MediaExtractor videoMediaExtractor = new MediaExtractor();
        MediaExtractor audioMediaExtractor = new MediaExtractor();

        try {
            videoMediaExtractor.setDataSource(videoPath + "/1.mp4");
            audioMediaExtractor.setDataSource(videoPath + "/testfile.mp4");

            int videoTrackCount = videoMediaExtractor.getTrackCount();
            int audioTrackCount = audioMediaExtractor.getTrackCount();

            int videoTrackIndex = -1;
            int audioTrackIndex = -1;

            for (int i = 0; i < videoTrackCount; i++) {
                MediaFormat trackFormat = videoMediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            for (int i = 0; i < audioTrackCount; i++) {
                MediaFormat trackFormat = audioMediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }

            MediaFormat videoTrackFormat = videoMediaExtractor.getTrackFormat(videoTrackIndex);
            MediaFormat audioTrackFormat = audioMediaExtractor.getTrackFormat(audioTrackIndex);

            videoMediaExtractor.selectTrack(videoTrackIndex);
            audioMediaExtractor.selectTrack(audioTrackIndex);

            MediaMuxer mediaMuxer = new MediaMuxer(videoPath + "/output.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            int videoAddTrackIndex = mediaMuxer.addTrack(videoTrackFormat);
            int audioAddTrackIndex = mediaMuxer.addTrack(audioTrackFormat);

            mediaMuxer.start();

            int frameSize = videoTrackFormat.getInteger(MediaFormat.KEY_FRAME_RATE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            bufferInfo.presentationTimeUs = 0;
            while (true) {
                int readSampleData = videoMediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleData < 0)
                    break;
                bufferInfo.size = readSampleData;
                bufferInfo.flags = videoMediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += 1000 * 1000 / frameSize;
                mediaMuxer.writeSampleData(videoAddTrackIndex, byteBuffer, bufferInfo);
                videoMediaExtractor.advance();
            }

            bufferInfo.presentationTimeUs = 0;
            while (true) {
                int readSampleData = audioMediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleData < 0)
                    break;
                bufferInfo.size = readSampleData;
                bufferInfo.flags = audioMediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += 1000 * 1000 / frameSize;
                mediaMuxer.writeSampleData(audioAddTrackIndex, byteBuffer, bufferInfo);
                audioMediaExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            videoMediaExtractor.release();
            audioMediaExtractor.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
