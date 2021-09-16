package com.example.harangT.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.harangT.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private BackPressedEvent backPressedEvent;

    public static String StudentId;
    public static String s_id;
    public static String s_name;
    public static HashMap<String,HashMap<String,String>> totalConcentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_base);

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
        setFragment(0);//첫 프래그먼트 화면 지정


    }

    //프래그먼트 교체 실행
    private void setFragment(int n){
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