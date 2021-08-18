package com.example.harangT.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.example.harangT.rtmp.PathUtils;

import java.io.File;


import com.example.harangT.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RtmpStreamActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {
    private RtmpCamera1 rtmpCamera1;
    private Button btnStart;
    private Button btnStop;
    private String staticUrl;
    private String p_id;
    private String streamId;

    private String currentDateAndTime = "";
    private File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.p_activity_rtmp);

        Intent intent = getIntent();
        staticUrl = intent.getStringExtra("url");
        p_id = intent.getStringExtra("p_id");
        streamId = intent.getStringExtra("streamId");

        folder = PathUtils.getRecordPath(this);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnStop = findViewById(R.id.b_stop);
        btnStop.setOnClickListener(this);
        Button switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        rtmpCamera1 = new RtmpCamera1(surfaceView, this);
        rtmpCamera1.setReTries(10);
        surfaceView.getHolder().addCallback(this);






    }

    @Override
    public void onConnectionStartedRtmp(String rtmpUrl) {
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpStreamActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtmpCamera1.reTry(7000, reason)) {
                    Toast.makeText(RtmpStreamActivity.this, "Retry", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(RtmpStreamActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                            .show();
                    rtmpCamera1.stopStream();
                    btnStart.setText(R.string.start_button);
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpStreamActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpStreamActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpStreamActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startStream(){
        if (!rtmpCamera1.isStreaming()) {
            if (rtmpCamera1.isRecording()
                    || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                btnStart.setText(R.string.stop_button);
                //db에서 activate 수정
                rtmpCamera1.startStream(staticUrl);
                Toast.makeText(this, "시작 : "+staticUrl,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error preparing stream, This device cant do it",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            btnStart.setText(R.string.start_button);
            rtmpCamera1.stopStream();
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtmpCamera1.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            btnStart.setText(getResources().getString(R.string.start_button));
        }
        rtmpCamera1.stopPreview();
    }

    private void activateVideo(String v_id){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                    } else {
                        Log.i("db_test", "server connect fail");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("db_test", "catch : " + e.getMessage());
                }

            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        RtmpActivateRequest request = new RtmpActivateRequest(v_id, responseListener);
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void deActivateVideo(String v_id){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                    } else {
                        Log.i("db_test", "server connect fail");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("db_test", "catch : " + e.getMessage());
                }

            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        RtmpDeactivateRequest request = new RtmpDeactivateRequest(v_id, responseListener);
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                if (!rtmpCamera1.isStreaming()) {
                    if (rtmpCamera1.isRecording()
                            || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                        btnStart.setVisibility(View.GONE);
                        btnStop.setVisibility(View.VISIBLE);
                        activateVideo(streamId);
                        rtmpCamera1.startStream(staticUrl);
                        Toast.makeText(this, "시작 : "+staticUrl,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error preparing stream, This device cant do it",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.switch_camera:
                try {
                    rtmpCamera1.switchCamera();
                } catch (CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.b_stop:
                rtmpCamera1.stopStream();
                deActivateVideo(streamId);
                Log.d("rtmpTest","정상적으로 종료 됨");
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.app_icon)
                .setTitle("스트리밍 종료")
                .setMessage("스트리밍을 종료하시겠습니까?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요", null)
                .show();
    }
}
