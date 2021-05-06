package com.example.harang.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harang.GazeTrackerManager;
import com.example.harang.R;

import camp.visual.gazetracker.util.ViewLayoutChecker;

public class TestActivity extends AppCompatActivity {
    //texture view 재생
    protected TextureView mTextureView = null;
    protected MediaPlayer mPlayer = null;

    //eyetracking
    private static final String TAG = TestActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA // 시선 추적 input
    };
    private static final int REQ_PERMISSION = 1000;
    private GazeTrackerManager gazeTrackerManager;
    private ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private HandlerThread backgroundThread = new HandlerThread("background");
    private Handler backgroundHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_test);
        // Get controls
        getControls();
        // Prepare video for TextureView
        prepareTextureViewVideo();
    }

    protected void getControls() {
        // Get buttons
        Button btnPlay = (Button) findViewById(R.id.btnPlayTV);
        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTextureView();
            }
        });
        Button btnStop = (Button) findViewById(R.id.btnStopTV);
        btnStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTextureView();
            }
        });

        // Get views
        mTextureView = (TextureView) findViewById(R.id.screenTextureView);

        //get gazetracker manager
        gazeTrackerManager = GazeTrackerManager.makeNewInstance(this);
        //initView();
        //checkPermission();
        //initHandler();
    }

    /////////////////////////////////////////////////////////////////////////////////
    // MediaPlayer related code from here
    /////////////////////////////////////////////////////////////////////////////////

    protected class MyTexureViewListener implements TextureView.SurfaceTextureListener {
        Context mContext;
        String mVideoSource;

        public MyTexureViewListener(Context context, String source) {
            mContext = context;
            mVideoSource = source;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            try {
                // Create MediaPlayer
                mPlayer = new MediaPlayer();

                // Set the surface
                Surface surface = new Surface(surfaceTexture);
                mPlayer.setSurface(surface);

                // Set the video source
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + mVideoSource);
                mPlayer.setDataSource(mContext, uri);

                // Prepare: In case of local file prepare() can be used, but for streaming, prepareAsync() is a must
                mPlayer.prepareAsync();

                // Wait for the preparation
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Play the video
                        playTextureView();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) { return false; }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
    }

    protected void prepareTextureViewVideo() {
        // Set the listener to play "sample.mp4" in raw directory
        mTextureView.setSurfaceTextureListener(new MyTexureViewListener(this, "homework"));
    }

    protected void playTextureView() {
        // Play it
        mPlayer.start();
    }

    protected void stopTextureView() {
        // Pause it. If stopped, mPlayer should be prepared again.
        mPlayer.pause();
    }

}
