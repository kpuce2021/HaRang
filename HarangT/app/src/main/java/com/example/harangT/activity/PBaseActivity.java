package com.example.harangT.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.harangT.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PBaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ProfessorFragment1 professorFragment1;
    private ProfessorFragment2 professorFragment2;
    private ProfessorFragment3 professorFragment3;
    private ProfessorFragment4 professorFragment4;
    private ProfessorFragment5 professorFragment5;
    private BackPressedEvent backPressedEvent;

    public static String ProfessorId;
    public static String p_id;
    public static String p_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_base);

        Intent intent = getIntent();
        ProfessorId = intent.getStringExtra("user_id");
        p_id = intent.getStringExtra("user_primaryKey");
        p_name = intent.getStringExtra("p_name");

        backPressedEvent = new BackPressedEvent(this);
        Log.d("p_Base",p_id);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.action_upload:
                    setFragment(0);
                    break;
                case R.id.action_streaming:
                    setFragment(1);
                    break;
                case R.id.action_list:
                    setFragment(2);
                    break;
                case R.id.action_stu_set:
                    setFragment(3);
                    break;
                case R.id.action_setting:
                    setFragment(4);
                    break;
            }
            return true;

        });
        professorFragment1 = new ProfessorFragment1();
        professorFragment2 = new ProfessorFragment2();
        professorFragment3 = new ProfessorFragment3();
        professorFragment4 = new ProfessorFragment4();
        professorFragment5 = new ProfessorFragment5();
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
            case 4:
                ft.replace(R.id.main_frame, professorFragment5);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       // backPressedEvent.onBackPressed();
    }
}