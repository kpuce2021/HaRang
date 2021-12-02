package com.example.harangS.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class ClipAudioConverter {

    private static boolean loaded;

    private Context context;
    private File audioFile;
    private AudioFormat format;
    private IConvertCallback callback;
    private static String Tag = "FFMPEG";
    private static String startTime;
    private static String endTime;

    private ClipAudioConverter(Context context) {
        this.context = context;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void load(final Context context, final String videoName, final String v_id, final ArrayList<HashMap<String,String>> clipVideoMap, final int position) {
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() { }

                @Override
                public void onSuccess() {
                    Log.i(Tag, "Binary load success");
                    cmdExcute(context, videoName, v_id, clipVideoMap, position);
                }

                @Override
                public void onFailure() { Log.i(Tag, "Binary load Fail"); }

                @Override
                public void onFinish() { }
            });
        } catch (Exception e) {
            loaded = false;
//            callback.onFailure(e);
        }
    }

    private static void cmdExcute(Context context, String videoName, String v_id, ArrayList<HashMap<String,String>> clipVideoMap, int position) {
        String fullVideoPath = context.getExternalFilesDir(null).toString()  + "/" + videoName+".mp4";
        String clipPath = context.getExternalFilesDir(null).toString()  + "/" + v_id+"-"+position+".mp4";

        try {
            Log.i("ffmpeg","filePath : "+fullVideoPath);
            //Log.i("ffmpeg","startTime : "+startTime);
            //Log.i("ffmpeg","endTime : "+endTime);
            Log.i("ffmpeg","clipPath : "+clipPath);
            File files = new File(fullVideoPath);
            if(files.exists()==true) { //파일이 있을시
                Runtime.getRuntime().exec("rm " + clipPath);
            }
            startEndTime(clipVideoMap.get(position).get("cv_stime"), clipVideoMap.get(position).get("cv_etime"));
            String[] cmd = {"-i", fullVideoPath, "-ss", startTime, "-to", endTime, clipPath };
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e("ffmpeg harang", "success");
                    Intent intent = new Intent(context, ClipVideoActivity.class);
                    intent.putExtra("VideoName", videoName);
                    intent.putExtra("studentId", BaseActivity.StudentId);
                    intent.putExtra("s_id", BaseActivity.s_id);
                    intent.putExtra("v_id", v_id);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }

                @Override
                public void onProgress(String message) {
                    Log.e("ffmpeg harang", "progress..");
                }

                @Override
                public void onFailure(String message) {
                    Log.e("ffmpeg harang", "failure"+message);
                }

                @Override
                public void onStart() {
                    Log.e("ffmpeg harang", "start");
                }

                @Override
                public void onFinish() {
                    Log.e("ffmpeg harang", "finish");

                }
            });

        } catch (Exception e) {

        }
    }
    private static void videoInFolder(String filePath,String videoName){
        String path = filePath ;
        File directory = new File(path);
        File[] files = directory.listFiles();

        boolean temp = false;

        for(int i=0;i<files.length;i++){
            if(files[i].getName().equals(videoName+".mp4")){
                temp = true;
            }
        }
        if (temp) {
            Log.i("ffmpeg","영상 경로 올바르게 들어감");
            Log.i("ffmpeg",filePath+videoName);
        }else{
            Log.i("ffmpeg","영상 없음");
        }
    }

    private static void startEndTime(String s_time, String e_time){
        int startTimem = Integer.parseInt(s_time);
        int endTimem= Integer.parseInt(e_time);
        int[] timeList = new int[3]; //0 : 시, 1 : 분, 2 : 초
        //시작시간
        timeList[2] = startTimem;
        timeList[1] = timeList[2] / 60;
        timeList[0] = timeList[1] / 60;
        timeList[2] = timeList[2] % 60;
        timeList[1] = timeList[1] % 60;
        startTime = String.format("%02d",timeList[0]) + ":" + String.format("%02d",timeList[1]) + ":" +String.format("%02d",timeList[2]) ;

        //종료시간
        timeList[2] = endTimem;
        timeList[1] = timeList[2] / 60;
        timeList[0] = timeList[1] / 60;
        timeList[2] = timeList[2] % 60;
        timeList[1] = timeList[1] % 60;
        endTime = String.format("%02d",timeList[0]) + ":" + String.format("%02d",timeList[1]) + ":" +String.format("%02d",timeList[2]) ;


    }


    public static ClipAudioConverter with(Context context) {
        return new ClipAudioConverter(context);
    }

    public ClipAudioConverter setFile(File originalFile) {
        this.audioFile = originalFile;
        return this;
    }

    public ClipAudioConverter setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public ClipAudioConverter setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert() {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        if (audioFile == null || !audioFile.exists()) {
            callback.onFailure(new IOException("File not exists"));
            return;
        }
        if (!audioFile.canRead()) {
            callback.onFailure(new IOException("Can't read the file. Missing permission?"));
            return;
        }
        final File convertedFile = getConvertedFile(audioFile, format);
        final String[] cmd = new String[]{"-y", "-i", audioFile.getPath(), convertedFile.getPath()};
        try {
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(convertedFile);
                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(new IOException(message));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format) {
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }
}