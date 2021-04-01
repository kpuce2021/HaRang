package com.example.harang.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harang.GazeTrackerManager;
import com.example.harang.R;
import com.example.harang.view.GazePathView;

import java.io.IOException;
import java.io.InputStream;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.util.ViewLayoutChecker;

public class DemoActivity extends AppCompatActivity {
  private static final String TAG = DemoActivity.class.getSimpleName();
  private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
  private GazePathView gazePathView;
  private GazeTrackerManager gazeTrackerManager;
  private TextView textView1;
  GazeInfo gazeInfo = null;
  private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
      2, 30, 0.5F, 0.001F, 1.0F);

  private ConcentrateManager concentrateManager;
  private VideoView videoView;
  private MediaController mediaController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);
    textView1 = findViewById(R.id.textView);
    gazeTrackerManager = GazeTrackerManager.getInstance();
    Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());


    concentrateManager = ConcentrateManager.getInstance();
    //이전 객체에서 저장했던 값이 의도대로 잘 불러와짐
    //concentrateManager.printTest();


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
  }

  private void initView() {
    gazePathView = findViewById(R.id.gazePathView);

    videoView = findViewById(R.id.videoView);
    mediaController = new MediaController(this);
    mediaController.setAnchorView(videoView);
    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/home");
    videoView.setMediaController(mediaController);
    videoView.setVideoURI(uri);
    videoView.start();
  }
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
      /*
      if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
        float[] filtered = oneEuroFilterManager.getFilteredValues();
        gazePathView.onGaze(filtered[0], filtered[1], gazeInfo.eyeMovementState == EyeMovementState.FIXATION);
        
        //이 부분에서 concentrateManager 실행
      }
       */
      Log.i(TAG, "check eyeMovement " + gazeInfo.eyeMovementState);
      Log.i(TAG, "screenState " + gazeInfo.screenState);
    }

  };
}
