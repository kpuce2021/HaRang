package com.example.harangS.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.harangS.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private StudentFragment1 studentFragment1;
    private StudentFragment2 studentFragment2;
    private StudentFragment3 studentFragment3;
    private StudentFragment4 studentFragment4;
    private StudentFragment5 studentFragment5;

    private BackPressedEvent backPressedEvent;

    public static String StudentId;
    public static String s_id;
    public static String s_name;
    public static HashMap<String,HashMap<String,String>> totalConcentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Intent intent = getIntent();
        StudentId = intent.getStringExtra("user_id");
        s_id = intent.getStringExtra("s_id");
        s_name = intent.getStringExtra("s_name");
        totalConcentList = new HashMap<>();

        backPressedEvent = new BackPressedEvent(this);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        setFragment(0);
                        break;
                    case R.id.action_RTMP:
                        setFragment(1);
                        break;
                    case R.id.action_concent:
                        setFragment(2);
                        break;
                    case R.id.action_video:
                        setFragment(3);
                        break;
                    case R.id.action_setting:
                        setFragment(4);
                        break;
                }
                return true;

            }
        });
        studentFragment1 = new StudentFragment1();
        studentFragment2 = new StudentFragment2();
        studentFragment3 = new StudentFragment3();
        studentFragment4 = new StudentFragment4();
        studentFragment5 = new StudentFragment5();
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
            case 3:
                ft.replace(R.id.main_frame, studentFragment4);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame, studentFragment5);
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

/*    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean k_f = intent.getBooleanExtra("kill", false);
        if(k_f == true){
            finish();
        }
    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //backPressedEvent.onBackPressed();
    }
}