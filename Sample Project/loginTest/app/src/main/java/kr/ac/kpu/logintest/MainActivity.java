package kr.ac.kpu.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tv_id, tv_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_id = findViewById(R.id.tv_id);
        tv_pass = findViewById(R.id.tv_pass);


        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String password = intent.getStringExtra("password");

        tv_id.setText(id);
        tv_pass.setText(password);

    }
}