package com.example.harang.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment5 extends Fragment {
    private View view;
    private TextView tv_name;
    private LinearLayout ll_logout;
    private static String p_name = p_BaseActivity.p_name;
    public ProfessorFragment5(){ }
    public static ProfessorFragment5 newInstance() {
        return new ProfessorFragment5();
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
        view = inflater.inflate(R.layout.p_fragment5, container, false);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, 1);
        }

        Button btnStreaming = view.findViewById(R.id.DefaultRTMP);
        Button btnFile = view.findViewById(R.id.FileRTMP);

        //defaultRTMP
        btnStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RtmpStreamActivity.class);
                startActivity(intent);
            }
        });

        //FileRTMP
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RtmpFileActivity.class);
                startActivity(intent);
            }
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