package com.example.harang.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harang.GazeTrackerManager;
import com.example.harang.R;
import com.example.harang.view.CalibrationViewer;
import com.example.harang.view.GazePathView;
import com.example.harang.view.PointView;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.util.ViewLayoutChecker;

public class VideoActivity extends Activity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;
    private ConcentrateManager concentrateManager;
    private VideoView videoView;
    private MediaController mediaController;
    private static String playTitle;

    private PointView viewPoint;
    private CalibrationViewer viewCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        gazeTrackerManager = GazeTrackerManager.getInstance();
        Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());

        concentrateManager = ConcentrateManager.makeNewInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gazeTrackerManager.startGazeTracking();
        setOffsetOfView();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        gazeTrackerManager.stopGazeTracking();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        gazeTrackerManager.removeCallbacks(gazeCallback);
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        concentrateManager.printTest();
    }

    private void initView() {
        gazePathView = findViewById(R.id.s3gazePathView);
        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);

        /*
        // VideoView : 동영상을 재생하는 뷰
        videoView = findViewById(R.id.s3VideoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController); // Video View 에 사용할 컨트롤러 지정

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/raw/home");

        videoView.setVideoURI(uri);
        videoView.start();
        */


        Intent intent = getIntent();
        playTitle = intent.getStringExtra("title");

        // VideoView : 동영상을 재생하는 뷰
        videoView = (VideoView) findViewById(R.id.s3VideoView);

        // MediaController : 특정 View 위에서 작동하는 미디어 컨트롤러 객체
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController); // Video View 에 사용할 컨트롤러 지정

        String path = getExternalFilesDir(null).toString()  + "/"; // 기본적인 절대경로 얻어오기


        // 절대 경로 = SDCard 폴더 = "stroage/emulated/0"
        //          ** 이 경로는 폰마다 다를수 있습니다.**
        // 외부메모리의 파일에 접근하기 위한 권한이 필요 AndroidManifest.xml에 등록
        Log.d("test", "절대 경로 : " + path);

        videoView.setVideoPath(path+playTitle);
        // VideoView 로 재생할 영상
        // 아까 동영상 [상세정보] 에서 확인한 경로
        videoView.requestFocus(); // 포커스 얻어오기
        videoView.start(); // 동영상 재생



    }

    //원래 데모 버전
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(gazePathView, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                gazePathView.setOffset(x, y);
            }
        });
    }

    //main에 있던 버전
    //null point exception
    /*
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
                //viewCalibration.setOffset(x, y);
            }
        });
    }
     */


    private final GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            Log.i(TAG, gazeInfo.eyeMovementState+" "+gazeInfo.screenState+" "+gazeInfo.timestamp+" "+(Long.valueOf(gazeInfo.timestamp).intValue()/1000)%10000);
            //timestamp 값이 뒤에서 4번째 숫자가 초에 해당하는 부분임
            //아이트래킹 로그 출력부분

            //test중
            concentrateManager.getEyetrackingData(gazeInfo,(Long.valueOf(gazeInfo.timestamp).intValue()/1000)%10000); //아이트래킹 정보 저장
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
        }

    };
}
