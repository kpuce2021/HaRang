package com.example.harangS.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.harangS.R;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;
import java.util.Arrays;
import org.videolan.libvlc.MediaPlayer;

/**
 * Created by pedro on 25/06/17.
 */
public class StudentStreamRTMPActivity extends AppCompatActivity implements VlcListener, View.OnClickListener {

    private VlcVideoLibrary vlcVideoLibrary;
    private Button bStart;
    private Button bStop;


    private String staticUrl;

    private String[] options = new String[]{":fullscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.stu_activity_rtmp);
        
        Intent intent = getIntent();
        staticUrl = intent.getStringExtra("url");
        
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        bStart = (Button) findViewById(R.id.b_start);
        bStart.setOnClickListener(this);
        bStop = (Button) findViewById(R.id.b_stop);
        bStop.setOnClickListener(this);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
        bStart.setText("start_player");
    }

    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_start:
                if (!vlcVideoLibrary.isPlaying()) {
                    vlcVideoLibrary.play(staticUrl);
                    //아이트래킹 & db연결 시작
                    bStop.setVisibility(View.VISIBLE);
                    bStart.setVisibility(View.GONE);
                    Log.d("dbtest", "시작 : "+staticUrl);
                } else {
                    vlcVideoLibrary.stop();
                }
                break;
            case R.id.b_stop:
                vlcVideoLibrary.stop();
                //아이트래킹, db연결 끝
                Log.d("dbtest","정상적으로 종료 됨");
                finish();
                break;
            default:
                break;
        }

    }
}
