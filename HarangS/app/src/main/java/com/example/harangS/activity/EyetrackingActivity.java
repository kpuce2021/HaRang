package com.example.harangS.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.constant.AccuracyCriteria;
import camp.visual.gazetracker.constant.CalibrationModeType;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.ScreenState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import com.example.harangS.GazeTrackerManager;
import com.example.harangS.GazeTrackerManager.LoadCalibrationResult;
import com.example.harangS.R;
import com.example.harangS.view.CalibrationViewer;
import com.example.harangS.view.PointView;

public class EyetrackingActivity extends AppCompatActivity {
    private static final String TAG = EyetrackingActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA // 시선 추적 input
    };
    private static final int REQ_PERMISSION = 1000;
    private GazeTrackerManager gazeTrackerManager;
    private ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private HandlerThread backgroundThread = new HandlerThread("background");
    private Handler backgroundHandler;

    private static String studentId;
    private static String s_id;

    //test중
    private ConcentrateManager concentrateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        s_id = intent.getStringExtra("s_id");

        gazeTrackerManager = GazeTrackerManager.makeNewInstance(this); //생성을 이 부분에서만 실행
        //이 이후에 gazeManager를 이용할 경우 getInstance를 통해서 값 받아오기
        Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());

        initView();
        checkPermission();
        initHandler();

        //concentrateManager = new ConcentrateManager(this); //실행되는 버전
        //concentrateManager = ConcentrateManager.makeNewInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preview.isAvailable()) {
            // When if textureView available
            gazeTrackerManager.setCameraPreview(preview);
        }
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback, calibrationCallback, statusCallback);
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // 화면 전환후에도 체크하기 위해
        setOffsetOfView();
        gazeTrackerManager.startGazeTracking();
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
        //gazeTrackerManager.removeCameraPreview(preview);
        //gazeTrackerManager.removeCallbacks(gazeCallback, calibrationCallback, statusCallback);
        Log.i(TAG, "onStop");
        gazeTrackerManager.removeCallbacks(gazeCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //releaseHandler();
        //viewLayoutChecker.releaseChecker();
    }

    // handler

    private void initHandler() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void releaseHandler() {
        backgroundThread.quitSafely();
    }

    // handler end

    // permission
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permission status
            if (!hasPermissions(PERMISSIONS)) {

                requestPermissions(PERMISSIONS, REQ_PERMISSION);
            } else {
                checkPermission(true);
            }
        }else{
            checkPermission(true);
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermissions(String[] permissions) {
        int result;
        // Check permission status in string array
        for (String perms : permissions) {
            if (perms.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(this)) {
                    return false;
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false;
            }
        }
        // When if all permission allowed
        return true;
    }

    private void checkPermission(boolean isGranted) {
        if (isGranted) {
            permissionGranted();
        } else {
            showToast("not granted permissions", true);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermissionAccepted) {
                        checkPermission(true);
                    } else {
                        checkPermission(false);
                    }
                }
                break;
        }
    }

    private void permissionGranted() {
        initGaze();
    }
    // permission end

    // view
    private TextureView preview;
    private View layoutProgress;
    private View viewWarningTracking;
    private PointView viewPoint;
    private Button btnStartCalibration;
    private Button btnComplete;
    private CalibrationViewer viewCalibration;

    // gaze coord filter
    private boolean isUseGazeFilter = true;
    // calibration type
    private CalibrationModeType calibrationType = CalibrationModeType.DEFAULT;
    private AccuracyCriteria criteria = AccuracyCriteria.DEFAULT;

    private void initView() {

        layoutProgress = findViewById(R.id.layout_progress);
        layoutProgress.setOnClickListener(null);

        viewWarningTracking = findViewById(R.id.view_warning_tracking);

        preview = findViewById(R.id.preview);
        preview.setSurfaceTextureListener(surfaceTextureListener);


        btnStartCalibration = findViewById(R.id.btn_start_calibration);
        btnStartCalibration.setOnClickListener(onClickListener);
        btnComplete = findViewById(R.id.btn_complete);
        btnComplete.setOnClickListener(onClickListener);

        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);

        calibrationType = CalibrationModeType.SIX_POINT;
        criteria = AccuracyCriteria.DEFAULT;
        isUseGazeFilter = true;
        setOffsetOfView();
    }


    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // When if textureView available
            gazeTrackerManager.setCameraPreview(preview);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    // The gaze or calibration coordinates are delivered only to the absolute coordinates of the entire screen.
    // The coordinate system of the Android view is a relative coordinate system,
    // so the offset of the view to show the coordinates must be obtained and corrected to properly show the information on the screen.
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
                viewCalibration.setOffset(x, y);
            }
        });
    }

    private void showProgress() {
        if (layoutProgress != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void hideProgress() {
        if (layoutProgress != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutProgress.setVisibility(View.INVISIBLE);
                    //gazeTrackerManager.startCalibration(calibrationType, criteria);
                    startCalibration();
                }
            });
        }
    }

    private void showTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewWarningTracking.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideTrackingWarning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewWarningTracking.setVisibility(View.INVISIBLE);
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnStartCalibration) {
                startCalibration();
            }else if(v == btnComplete){
                finish();
            }
        }
    };

    private void showToast(final String msg, final boolean isShort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EyetrackingActivity.this, msg, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showGazePoint(final float x, final float y, final ScreenState type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPoint.setType(type == ScreenState.INSIDE_OF_SCREEN ? PointView.TYPE_DEFAULT : PointView.TYPE_OUT_OF_SCREEN);
                viewPoint.setPosition(x, y);
            }
        });
    }

    private void setCalibrationPoint(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setVisibility(View.VISIBLE);
                viewCalibration.changeDraw(true, null);
                viewCalibration.setPointPosition(x, y);
                viewCalibration.setPointAnimationPower(0);
            }
        });
    }

    private void setCalibrationProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setPointAnimationPower(progress);
            }
        });
    }

    private void hideCalibrationView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCalibration.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setViewAtGazeTrackerState() {
        Log.i(TAG, "gaze : " + isTrackerValid() + ", tracking " + isTracking());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnStartCalibration.setEnabled(isTracking());

                if (!isTracking()) {
                    hideCalibrationView();
                }
            }
        });
    }

    // view end

    // gazeTracker
    private boolean isTrackerValid() {
        return gazeTrackerManager.hasGazeTracker();
    }

    private boolean isTracking() {
        return gazeTrackerManager.isTracking();
    }

    private final InitializationCallback initializationCallback = new InitializationCallback() {
        @Override
        public void onInitialized(GazeTracker gazeTracker, InitializationErrorType error) {
            if (gazeTracker != null) {
                initSuccess(gazeTracker);
            } else {
                initFail(error);
            }
        }
    };

    private void initSuccess(GazeTracker gazeTracker) {
        startTracking();
        hideProgress();

    }

    private void initFail(InitializationErrorType error) {
        hideProgress();
    }

    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(2);
    private final GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            processOnGaze(gazeInfo);
        }
    };

    private void processOnGaze(GazeInfo gazeInfo) {
        if (gazeInfo.trackingState == TrackingState.SUCCESS) {
            hideTrackingWarning();
            if (!gazeTrackerManager.isCalibrating()) {
                float[] filtered_gaze = filterGaze(gazeInfo);
                showGazePoint(filtered_gaze[0], filtered_gaze[1], gazeInfo.screenState);
            }
        } else {
            showTrackingWarning();
        }
    }

    private float[] filterGaze(GazeInfo gazeInfo) {
        if (isUseGazeFilter) {
            if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
                return oneEuroFilterManager.getFilteredValues();
            }
        }
        return new float[]{gazeInfo.x, gazeInfo.y};
    }

    private CalibrationCallback calibrationCallback = new CalibrationCallback() {
        @Override
        public void onCalibrationProgress(float progress) {
            setCalibrationProgress(progress);
        }

        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
            setCalibrationPoint(x, y);
            // Give time to eyes find calibration coordinates, then collect data samples
            backgroundHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCollectSamples();
                }
            }, 1000);
        }

        @Override
        public void onCalibrationFinished(double[] calibrationData) {
            // When calibration is finished, calibration data is stored to SharedPreference

            hideCalibrationView();
            showToast("초점 조절을 완료했습니다.", true);
        }
    };

    private StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onStarted() {
            // isTracking true
            // When if camera stream starting
            setViewAtGazeTrackerState();
        }

        @Override
        public void onStopped(StatusErrorType error) {
            // isTracking false
            // When if camera stream stopping
            setViewAtGazeTrackerState();
            if (error != StatusErrorType.ERROR_NONE) {
                switch (error) {
                    case ERROR_CAMERA_START:
                        // When if camera stream can't start
                        showToast("ERROR_CAMERA_START ", false);
                        break;
                    case ERROR_CAMERA_INTERRUPT:
                        // When if camera stream interrupted
                        showToast("ERROR_CAMERA_INTERRUPT ", false);
                        break;
                }
            }
        }
    };

    private void initGaze() {
        showProgress();
        gazeTrackerManager.initGazeTracker(initializationCallback);
    }

    private void releaseGaze() {
        gazeTrackerManager.deinitGazeTracker();
        setViewAtGazeTrackerState();
    }

    private void startTracking() {
        gazeTrackerManager.startGazeTracking();
    }

    private void stopTracking() {
        gazeTrackerManager.stopGazeTracking();

    }

    private boolean startCalibration() {
        boolean isSuccess = gazeTrackerManager.startCalibration(calibrationType, criteria);
        if (!isSuccess) {
            showToast("초점 조절을 실패했습니다.", false);
        }
        setViewAtGazeTrackerState();
        return isSuccess;
    }

    // Collect the data samples used for calibration
    private boolean startCollectSamples() {
        boolean isSuccess = gazeTrackerManager.startCollectingCalibrationSamples();
        setViewAtGazeTrackerState();
        return isSuccess;
    }

    private void stopCalibration() {
        gazeTrackerManager.stopCalibration();
        hideCalibrationView();
        setViewAtGazeTrackerState();
    }

    private void setCalibration() {
        LoadCalibrationResult result = gazeTrackerManager.loadCalibrationData();
        switch (result) {
            case SUCCESS:
                showToast("setCalibrationData success", false);
                break;
            case FAIL_DOING_CALIBRATION:
                showToast("calibrating", false);
                break;
            case FAIL_NO_CALIBRATION_DATA:
                showToast("Calibration data is null", true);
                break;
            case FAIL_HAS_NO_TRACKER:
                showToast("No tracker has initialized", true);
                break;
        }
        setViewAtGazeTrackerState();
    }
}
