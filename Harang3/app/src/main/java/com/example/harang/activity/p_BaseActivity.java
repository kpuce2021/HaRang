package com.example.harang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.harang.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class p_BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ProfessorFragment1 professorFragment1;
    private ProfessorFragment2 professorFragment2;
    private ProfessorFragment3 professorFragment3;
    private ProfessorFragment4 professorFragment4;

    public static String ProfessorId;
    public static String p_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_base);

        Intent intent = getIntent();
        ProfessorId = intent.getStringExtra("user_id");
        p_id = intent.getStringExtra("user_primaryKey");
        //p_id = intent.getStringExtra("p_id");

        Log.d("p_Base",p_id);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_upload:
                        setFragment(0);
                        break;
                    case R.id.action_list:
                        setFragment(1);
                        break;
                    case R.id.action_stu_set:
                        setFragment(2);
                        break;
                    case R.id.action_setting:
                        setFragment(3);
                        break;
                }
                return true;

            }
        });
        professorFragment1 = new ProfessorFragment1();
        professorFragment2 = new ProfessorFragment2();
        professorFragment3 = new ProfessorFragment3();
        professorFragment4 = new ProfessorFragment4();
        setFragment(0);//첫 프래그먼트 화면 지정
    }

    //프래그먼트 교체 실행
    private void setFragment(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame, professorFragment1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, professorFragment2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, professorFragment3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, professorFragment4);
                ft.commit();
                break;
        }
    }
    public void replaceFragment(Fragment fragment, Bundle bundle){ //bundle이 없으면 null값 전달
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.main_frame, fragment).commit();
    }
}