package com.example.harang.activity;

import android.app.Application;
import android.os.Environment;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import com.example.harang.activity.FfmpegActivity;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //AndroidAudioConverter.load(this, FfmpegActivity.path + FfmpegActivity.playTitle);
    }
}