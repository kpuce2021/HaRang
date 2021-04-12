package com.example.harang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.harang.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private StudentFragment1 studentFragment1;
    private StudentFragment2 studentFragment2;
    private StudentFragment3 studentFragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

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

}