package com.example.harangT.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangT.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment2 extends Fragment {
    private View view;
    private TextView tv_name;
    private LinearLayout ll_logout;
    private static String p_name = PBaseActivity.p_name;
    public static String p_id;
    private static String streamId;

    public ProfessorFragment2(){ }
    public static ProfessorFragment2 newInstance() {
        return new ProfessorFragment2();
    }

    private static Context mContext;
    private static Activity mActivity;

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment2, container, false);
        p_id = PBaseActivity.p_id;

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, 1);
        }

        Button btnStreaming = view.findViewById(R.id.DefaultRTMP);
        Button btnFile = view.findViewById(R.id.FileRTMP);
        EditText videoName = view.findViewById(R.id.edtVideoNameF);


        //defaultRTMP
        btnStreaming.setOnClickListener(v -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        streamId = jsonObject.getString("streamId");
                        Intent intent = new Intent(mContext, RtmpStreamActivity.class);
                        intent.putExtra("url", "rtmp://44.196.58.43/live/"+p_id+"to"+ streamId);
                        intent.putExtra("p_id", p_id);
                        intent.putExtra("streamId", streamId);
                        startActivity(intent);
                    } else {
                        Log.i("db_test", "server connect fail");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("db_test", "catch : " + e.getMessage());
                }

            };

            ProfessorCreateStreamRequest request = new ProfessorCreateStreamRequest(p_id, videoName.getText().toString(), responseListener);
            request.setShouldCache(false);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(request);


        });

        //FileRTMP
        btnFile.setOnClickListener(v -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        streamId = jsonObject.getString("streamId");
                        Intent intent = new Intent(mContext, RtmpFileActivity.class);
                        intent.putExtra("url", "rtmp://44.196.58.43/live/"+p_id+"to"+ streamId);
                        intent.putExtra("p_id", p_id);
                        intent.putExtra("streamId", streamId);
                        startActivity(intent);
                    } else {
                        Log.i("db_test", "server connect fail");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("db_test", "catch : " + e.getMessage());
                }

            };
            ProfessorCreateFileRequest request = new ProfessorCreateFileRequest(p_id, videoName.getText().toString(), responseListener);
            request.setShouldCache(false);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(request);


        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }

        super.onAttach(context);
    }


    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



}