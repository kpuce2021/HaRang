package com.example.harang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.harang.R;

public class VideoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        // VideoView : 동영상을 재생하는 뷰
        VideoView vv = (VideoView) findViewById(R.id.videoView1);

        // MediaController : 특정 View 위에서 작동하는 미디어 컨트롤러 객체
        MediaController mc = new MediaController(this);
        vv.setMediaController(mc); // Video View 에 사용할 컨트롤러 지정

        String path = getExternalFilesDir(null).toString()  + "/"; // 기본적인 절대경로 얻어오기


        // 절대 경로 = SDCard 폴더 = "stroage/emulated/0"
        //          ** 이 경로는 폰마다 다를수 있습니다.**
        // 외부메모리의 파일에 접근하기 위한 권한이 필요 AndroidManifest.xml에 등록
        Log.d("test", "절대 경로 : " + path);

        vv.setVideoPath(path+title);
        // VideoView 로 재생할 영상
        // 아까 동영상 [상세정보] 에서 확인한 경로
        vv.requestFocus(); // 포커스 얻어오기
        vv.start(); // 동영상 재생

    }
}
