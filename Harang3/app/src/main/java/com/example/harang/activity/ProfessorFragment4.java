package com.example.harang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment4 extends Fragment {
    private View view;
    private TextView tv_name;
    private LinearLayout ll_logout;
    public ProfessorFragment4(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment4, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

/*        final Button btnModify = view.findViewById(R.id.modify_info);

        btnModify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), MyInfoActivity.class);
                startActivity(intent);
            }
        });*/


        tv_name = view.findViewById(R.id.tv_name);
        ll_logout = view.findViewById(R.id.ll_logout);
        tv_name.setText(p_BaseActivity.p_name);

        ll_logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
       /* btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyInfoActivity.class);
                startActivity(intent);
            }
        });*/
       /* btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), AccountLoginActivity.class);
                //startActivity(intent);
            }
        });*/
    }
}