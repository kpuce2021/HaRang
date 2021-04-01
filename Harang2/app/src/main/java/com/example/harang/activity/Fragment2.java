package com.example.harang.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

public class Fragment2 extends Fragment {
    private View view;

    private Button btnDownload;
    private Button btnUpload;
    private Button btnShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment2, container, false);
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();

    }

    private void initUI() {
        btnDownload = view.findViewById(R.id.buttonDownloadMain);
        btnUpload = view.findViewById(R.id.buttonUploadMain);
        btnShow = view.findViewById(R.id.buttonShow);

        btnDownload.setOnClickListener(view -> startActivity(new Intent(getContext(), DownloadActivity.class)));
        btnUpload.setOnClickListener(view -> startActivity(new Intent(getContext(), UploadActivity.class)));
        /*btnShow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowActivity.class);
                startActivity(intent);
            }
        });*/
         btnShow.setOnClickListener(view -> startActivity(new Intent(getContext(), ShowActivity.class)));
    }
}
