package com.example.harang.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

public class Fragment1 extends Fragment {
    private View view;
    private TextView tv_id, tv_pass;
    String user_id, user_pass;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);
        tv_id = view.findViewById(R.id.tv_id);
        tv_pass = view.findViewById(R.id.tv_pass);
        Bundle bundle = this.getArguments();
        if(bundle != null){
             bundle = getArguments();
             user_id = bundle.getString("user_id");
             user_pass = bundle.getString("user_pass");
             tv_id.setText(user_id);
             tv_pass.setText(user_pass);
        }

       return view;
    }
}
