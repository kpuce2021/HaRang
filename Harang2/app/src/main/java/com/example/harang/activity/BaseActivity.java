package com.example.harang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.harang.GazeTrackerManager;
import com.example.harang.R;
import com.example.harang.activity.Fragment1;
import com.example.harang.activity.Fragment2;
import com.example.harang.activity.Fragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private GazeTrackerManager gazeTrackerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        gazeTrackerManager = GazeTrackerManager.makeNewInstance(this); //생성을 이 부분에서만 실행
        bottomNavigationView = findViewById(R.id.bottomNavi);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        String user_pass = intent.getStringExtra("user_pass");
        Log.d("minu",user_id);
        Log.d("minu",user_pass);

        fragment1 = new Fragment1();

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("user_pass",user_pass);
        fragment1.setArguments(bundle);

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
        //fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        setFragment(0);//첫 프래그먼트 화면 지정
    }

    //프래그먼트 교체 실행
    private void setFragment(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame, fragment1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, fragment2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, fragment3);
                ft.commit();
                break;
        }
    }

}