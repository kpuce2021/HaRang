package com.example.harangT.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.pedro.encoder.input.decoder.AudioDecoderInterface;
import com.pedro.encoder.input.decoder.VideoDecoderInterface;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpFromFile;
import com.example.harangT.R;
import com.example.harangT.rtmp.PathUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RtmpFileActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, VideoDecoderInterface,
        AudioDecoderInterface, SeekBar.OnSeekBarChangeListener {

    private RtmpFromFile rtmpFromFile;
    private Button btnStart, bSelectFile, btnStop;
    private SeekBar seekBar;
    private TextView tvFile;
    private String filePath = "";
    private boolean touching = false;
    private String staticUrl;
    private String p_id;
    private String streamId;

    private String currentDateAndTime = "";
    private File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.p_activity_rtmp_file);

        Intent intent = getIntent();
        staticUrl = intent.getStringExtra("url");
        p_id = intent.getStringExtra("p_id");
        streamId = intent.getStringExtra("streamId");

        folder = PathUtils.getRecordPath(this);
        btnStart = findViewById(R.id.btnStart);
        bSelectFile = findViewById(R.id.b_select_file);
        btnStop = findViewById(R.id.fileBtnStop);
        btnStart.setOnClickListener(this);
        bSelectFile.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        tvFile = findViewById(R.id.tv_file);
        rtmpFromFile = new RtmpFromFile(this, this, this);
        seekBar.setOnSeekBarChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rtmpFromFile.isStreaming()) {
            rtmpFromFile.stopStream();
            btnStart.setText(getResources().getString(R.string.start_button));
        }
    }

    @Override
    public void onConnectionStartedRtmp(String rtmpUrl) {
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpFileActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpFileActivity.this, "Connection failed. " + reason,
                        Toast.LENGTH_SHORT).show();
                rtmpFromFile.stopStream();
                btnStart.setText(R.string.start_button);
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
                Toast.makeText(RtmpFileActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpFileActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RtmpFileActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && data != null) {
            filePath = PathUtils.getPath(this, data.getData());
            Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
            tvFile.setText(filePath);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                if (!rtmpFromFile.isStreaming()) {
                    try {
                        if (!rtmpFromFile.isRecording()) {
                            if (prepare()) {
                                btnStart.setVisibility(View.GONE);
                                btnStop.setVisibility(View.VISIBLE);
                                activateVideo(streamId);

                                rtmpFromFile.startStream(staticUrl);
                                seekBar.setMax(Math.max((int) rtmpFromFile.getVideoDuration(),
                                        (int) rtmpFromFile.getAudioDuration()));
                                updateProgress();
                            } else {
                                rtmpFromFile.stopStream();
                                Toast.makeText(this, "Error: unsupported file", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            btnStart.setText(R.string.stop_button);
                            activateVideo(streamId);
                            rtmpFromFile.startStream(staticUrl);
                            Toast.makeText(this, "시작됨 : " + staticUrl,
                                    Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {
                        //Normally this error is for file not found or read permissions
                        Toast.makeText(this, "Error: file not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnStart.setText(R.string.start_button);
                    rtmpFromFile.stopStream();
                }
                break;
            case R.id.b_select_file:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 5);
                break;
            //sometimes async is produced when you move in file several times
            case R.id.fileBtnStop:
                rtmpFromFile.stopStream();
                deActivateVideo(streamId);
                finish();
                break;

            default:
                break;
        }
    }

    private boolean prepare() throws IOException {
        boolean result = rtmpFromFile.prepareVideo(filePath);
        result |= rtmpFromFile.prepareAudio(filePath);
        return result;
    }

    private void updateProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (rtmpFromFile.isStreaming() || rtmpFromFile.isRecording()) {
                    try {
                        Thread.sleep(1000);
                        if (!touching) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(Math.max((int) rtmpFromFile.getVideoTime(),
                                            (int) rtmpFromFile.getAudioTime()));
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onVideoDecoderFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtmpFromFile.isStreaming()) {
                    btnStart.setText(R.string.start_button);
                    Toast.makeText(RtmpFileActivity.this, "Video stream finished", Toast.LENGTH_SHORT)
                            .show();
                    rtmpFromFile.stopStream();
                }
            }
        });
    }

    @Override
    public void onAudioDecoderFinished() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        touching = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (rtmpFromFile.isStreaming()) rtmpFromFile.moveTo(seekBar.getProgress());
        touching = false;
    }

    private void activateVideo(String v_id) {
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


    private void deActivateVideo(String v_id) {
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.app_icon)
                .setTitle("녹화 강의 중계 종료")
                .setMessage("녹화 강의 중계를 종료하시겠습니까?")
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
