package com.example.harangS.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.harangS.GazeTrackerManager;
import com.example.harangS.R;
import com.example.harangS.view.GazePathView;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;
import java.util.Arrays;
import org.videolan.libvlc.MediaPlayer;

import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.util.ViewLayoutChecker;

public class StreamRTMPActivity extends Activity implements VlcListener, View.OnClickListener {

    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;

    private VlcVideoLibrary vlcVideoLibrary;
    private Button bStart;
    private Button bStop;


    private String staticUrl;

    private String[] options = new String[]{":fullscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rtmp);


        gazeTrackerManager = GazeTrackerManager.getInstance();
        //concentrateManager 비슷한거 필요함


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
    protected void onStart() {
        super.onStart();
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
        gazePathView = findViewById(R.id.streamGazePathView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setOffsetOfView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gazeTrackerManager.stopGazeTracking();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gazeTrackerManager.removeCallbacks(gazeCallback);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //concentrateManager.accessDB();
    }


    //영상 재생
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
    public void onBuffering(MediaPlayer.Event event) { }
    //END 영상 재생




    //버튼 클릭
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_start:
                if (!vlcVideoLibrary.isPlaying()) {
                    vlcVideoLibrary.play(staticUrl);

                    bStop.setVisibility(View.VISIBLE);
                    bStart.setVisibility(View.GONE);
                    Log.d("dbtest", "시작 : "+staticUrl);


                    //eyetracking
                    //gazeTrackerManager.startGazeTracking();
                    try {
                        Class.forName("dalvik.system.CloseGuard")
                                .getMethod("setEnabled", boolean.class)
                                .invoke(null, true);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }

                    new Thread(() -> gazeTrackerManager.startGazeTracking()).start();


                    //db연결

                } else {
                    vlcVideoLibrary.stop();
                }
                break;
            case R.id.b_stop:
                vlcVideoLibrary.stop();
                //db연결 끝(??)
                finish();
                break;
            default:
                break;
        }

    }





    //아이트래킹
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(gazePathView, (x, y) -> gazePathView.setOffset(x, y));
    }


    private final GazeCallback gazeCallback = gazeInfo -> {
        //test중
        //concentrateManager.getEyetrackingData(gazeInfo,(Long.valueOf(gazeInfo.timestamp).intValue()/1000)%10000); //아이트래킹 정보 저장
        Log.i("printTests", gazeInfo.eyeMovementState+" "+gazeInfo.screenState+" "+gazeInfo.timestamp+" "+(Long.valueOf(gazeInfo.timestamp).intValue()/1000)%10000);
        /*
         * 1초
         * 1분 = 60초
         * 1시간 = 60분 = 3600초
         * 2시간 = 120분 = 7200초
         *
         * timestamp = 1616599777831
         * --> 9777 부분만 필요하므로
         * (timestamp/1000)%10000 부분이 초에 해당함.
         * */
    };
    //END 아이트래킹




}

