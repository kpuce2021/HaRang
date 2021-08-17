package com.example.harangT.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.harangT.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment4 extends Fragment {
    private View view;
    private TextView tv_name;
    private LinearLayout ll_logout;
    private static String p_name = p_BaseActivity.p_name;
    public ProfessorFragment4(){

    }
    public static ProfessorFragment4 newInstance() {
        return new ProfessorFragment4();
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
        tv_name.setText(p_name);

        ll_logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                btn_logout(v);
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

    public void btn_logout(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(getContext(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }
}