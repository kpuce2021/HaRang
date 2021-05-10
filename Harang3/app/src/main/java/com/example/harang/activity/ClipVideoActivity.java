package com.example.harang.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harang.R;

public class ClipVideoActivity extends AppCompatActivity {

    private static final String TAG = "clipVideo";

    private static String playTitle;
    private static String studentId;
    private static String s_id;
    private static String v_id;


    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipvideo);
    }
    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        /*
        1. 필요한 정보 : 비디오 이름, v_id, mSecList
        */
        Intent intent = getIntent();
        playTitle = intent.getStringExtra("VideoName");
        studentId = intent.getStringExtra("studentId");
        s_id = intent.getStringExtra("s_id");
        v_id = intent.getStringExtra("v_id");

        // VideoView : 동영상을 재생하는 뷰
        videoView = (VideoView) findViewById(R.id.clipVideoView);

        // MediaController : 특정 View 위에서 작동하는 미디어 컨트롤러 객체
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController); // Video View 에 사용할 컨트롤러 지정

        String path = getExternalFilesDir(null).toString()  + "/"; // 기본적인 절대경로 얻어오기


        // 절대 경로 = SDCard 폴더 = "stroage/emulated/0"
        //          ** 이 경로는 폰마다 다를수 있습니다.**
        // 외부메모리의 파일에 접근하기 위한 권한이 필요 AndroidManifest.xml에 등록
        Log.d(TAG, "절대 경로 : " + path);

        videoView.setVideoPath(path+playTitle+".mp4");
        // VideoView 로 재생할 영상
        // 아까 동영상 [상세정보] 에서 확인한 경로
        
        
        
        //영상 잘라서 재생
        
        
        
        videoView.requestFocus(); // 포커스 얻어오기
        videoView.start(); // 동영상 재생

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(getApplicationContext(),BaseActivity.class);
                intent.putExtra("user_id", studentId);
                intent.putExtra("s_id", s_id);
                startActivity(intent);
                finish();
            }
        });




    }
}
