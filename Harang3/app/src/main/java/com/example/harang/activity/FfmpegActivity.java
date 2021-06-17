package com.example.harang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.harang.R;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;

public class FfmpegActivity extends AppCompatActivity {

    public static String path;
    public static String playTitle;
    Button btn_ffmpeg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);

        btn_ffmpeg = findViewById(R.id.btn_ffmpeg);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        }

        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        playTitle = intent.getStringExtra("playTitle");

        btn_ffmpeg.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                try {
                    convertAudio(v,path,playTitle);
                } catch (FFmpegCommandAlreadyRunningException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        /*btn_ffmpeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    convertAudio(view,path,playTitle);
                } catch (FFmpegCommandAlreadyRunningException | IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    public void convertAudio(View v, String path, String playTitle) throws FFmpegCommandAlreadyRunningException, IOException {
        /**
         *  Update with a valid audio file!
         *  Supported formats: {@link AndroidAudioConverter.AudioFormat}
         */


        // File wavFile = new File(path,playTitle);
        //Log.e("test", wavFile.getAbsolutePath());
//        IConvertCallback callback = new IConvertCallback() {
//            @Override
//            public void onSuccess(File convertedFile) {
//                Toast.makeText(MainActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                Toast.makeText(MainActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        };
        Toast.makeText(this, "Converting audio file...", Toast.LENGTH_SHORT).show();
        //Runtime.getRuntime().exec("chmod -R 777 " + "/data/data/com.example.harang/files/ffmpeg");
        AndroidAudioConverter.load(this, path ,playTitle);
//       AndroidAudioConverter.with(this)
//                .setFile(wavFile)
//                .setFormat(AudioFormat.MP3)
//                .setCallback(callback)
//                .convert();
    }

}