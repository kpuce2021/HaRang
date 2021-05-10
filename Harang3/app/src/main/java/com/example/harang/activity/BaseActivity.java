package com.example.harang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.harang.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private StudentFragment1 studentFragment1;
    private StudentFragment2 studentFragment2;
    private StudentFragment3 studentFragment3;

    public static String StudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Intent intent = getIntent();
        StudentId = intent.getStringExtra("user_id");


        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        setFragment(0);
                        break;
                    case R.id.action_video:
                        setFragment(1);
                        break;
                    case R.id.action_setting:
                        setFragment(2);
                        break;
                }
                return true;

            }
        });
        studentFragment1 = new StudentFragment1();
        studentFragment2 = new StudentFragment2();
        studentFragment3 = new StudentFragment3();
        setFragment(0);//첫 프래그먼트 화면 지정


    }

    //프래그먼트 교체 실행
    private void setFragment(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame, studentFragment1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, studentFragment2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, studentFragment3);
                ft.commit();
                break;
        }
    }
    
    //탭 이외에 내부에서 내부 프래그먼트로 전환 시 사용
    /*
    *((BaseActivity)getActivity()).replaceFragment(  전환할 프래그먼트.newInstance()  );
    * */
    public void replaceFragment(Fragment fragment, Bundle bundle){ //bundle이 없으면 null값 전달
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.main_frame, fragment).commit();
    }

}