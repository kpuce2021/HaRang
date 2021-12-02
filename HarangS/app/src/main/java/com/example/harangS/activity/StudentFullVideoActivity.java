package com.example.harangS.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.harangS.GazeTrackerManager;
import com.example.harangS.R;
import com.example.harangS.view.GazePathView;

import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.util.ViewLayoutChecker;

import static camp.visual.gazetracker.state.ScreenState.OUTSIDE_OF_SCREEN;

public class StudentFullVideoActivity extends Activity {
    private static final String TAG = "StudentFullVideo";
    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;
    private ConcentrateManager concentrateManager;

    private VideoView videoView;
    private MediaController mediaController;
    private static String playTitle;
    private static String studentId;
    private static String s_id;
    private static String v_id;

    private MediaPlayer.OnPreparedListener PreParedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        gazeTrackerManager = GazeTrackerManager.getInstance();
        concentrateManager = ConcentrateManager.makeNewInstance(this);
        concentrateManager.getContext(StudentFullVideoActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gazeTrackerManager.startGazeTracking();
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
        Log.i(TAG, "onDestroy");
        concentrateManager.dialog.dismiss();
        concentrateManager.accessDB();
    }

    private void initView() {
        gazePathView = findViewById(R.id.s3gazePathView);

        Intent intent = getIntent();
        playTitle = intent.getStringExtra("VideoName");
        studentId = intent.getStringExtra("studentId");
        s_id = intent.getStringExtra("s_id");
        v_id = intent.getStringExtra("v_id");

        concentrateManager.getUserInfo(v_id,s_id, intent.getStringExtra("start1"),
                intent.getStringExtra("start2"), intent.getStringExtra("stop1"),
                intent.getStringExtra("stop2"), intent.getStringExtra("clipCount"));

        // VideoView : 동영상을 재생하는 뷰
        videoView = (VideoView) findViewById(R.id.s3VideoView);

        // MediaController : 특정 View 위에서 작동하는 미디어 컨트롤러 객체
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController); // Video View 에 사용할 컨트롤러 지정

        String path = getExternalFilesDir(null).toString()  + "/"; // 기본적인 절대경로 얻어오기


        // 절대 경로 = SDCard 폴더 = "stroage/emulated/0"
        //          ** 이 경로는 폰마다 다를수 있습니다.**
        // 외부메모리의 파일에 접근하기 위한 권한이 필요 AndroidManifest.xml에 등록
        Log.d(TAG, "절대 경로 : " + path);

        videoView.setVideoPath(path+playTitle);
        // VideoView 로 재생할 영상
        // 아까 동영상 [상세정보] 에서 확인한 경로
        videoView.requestFocus(); // 포커스 얻어오기

//        PreParedListener = mp -> mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
//            ConstraintLayout.LayoutParams lp =
//                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.FILL_PARENT,
//                            ConstraintLayout.LayoutParams.FILL_PARENT);
//            videoView.setLayoutParams(lp);
//        });
//        videoView.setOnPreparedListener(PreParedListener);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start(); // 동영상 재생
            }
        });
        videoView.start(); // 동영상 재생

        videoView.setOnCompletionListener(mp -> {
            gazeTrackerManager.stopGazeTracking();

            /*Intent intent = new Intent(VideoActivity.this, FfmpegActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("playTitle", playTitle);*/
            Intent intent1 = new Intent(getApplicationContext(),BaseActivity.class);
            intent1.putExtra("user_id", studentId);
            intent1.putExtra("s_id", s_id);
            startActivity(intent1);
            finish();
        });




    }

    private void getPlayTime(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        duration = timeInmillisec;

    }

    private static long duration;
    //현재시간 로그찍기 위함임, 삭제해도 무관
    private void current(){
        Log.i("printTest", "현재 시간"+videoView.getCurrentPosition()+"전체시간 : "+duration);

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


    private final GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            //test중
            concentrateManager.getEyetrackingData(gazeInfo,(Long.valueOf(gazeInfo.timestamp).intValue()/1000)%10000); //아이트래킹 정보 저장
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

            String path = getExternalFilesDir(null).toString()  + "/"; // 기본적인 절대경로 얻어오기
            getPlayTime(path+playTitle);
        }

    };
}
