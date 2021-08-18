package com.example.harangS.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangS.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText et_id, et_pass;
    private Button btn_login, btn_register;
    private RadioButton rb_professor_login, rb_student_login;
    private RadioGroup rg_login;
    private BackPressedEvent backPressedEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        rb_professor_login = findViewById(R.id.rb_professor_login);
        rb_student_login = findViewById(R.id.rb_student_login);
        rg_login = findViewById(R.id.rg_login);
        backPressedEvent = new BackPressedEvent(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 키보드가 focus된 edittext 밑으로 옴

        rg_login.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_student_login){
                    rb_student_login.setChecked(true);
                }else{
                    rb_professor_login.setChecked(true);
                }
            }
        });

        // 회원가입 버튼을 클릭 시 수행

        btn_register.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (rb_professor_login.isChecked()) {     // 교수가 회원가입을 하는 경우
                    login_professor();
                }

                if (rb_student_login.isChecked()) {       // 학생이 회원가입을 하는 경우
                    login_student();
                }
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { // edittext 작성 도중 edittext가 아닌 밖 터치 시 키보드 사라짐
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void login_professor(){
        String id = et_id.getText().toString();
        String password = et_pass.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("harang" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 로그인에 성공한 경우
                        String user_id = jsonObject.getString("id");
                        String user_pass = jsonObject.getString("password");
                        String user_primaryKey = jsonObject.getString("p_id");
                        String user_name = jsonObject.getString("p_name");


                        Log.d("minu",user_primaryKey);

                        Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, p_BaseActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("user_pass", user_pass);
                        intent.putExtra("user_primaryKey", user_primaryKey);
                        intent.putExtra("p_name", user_name);

                        startActivity(intent);
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ProfessorLoginRequest professorLoginRequest = new ProfessorLoginRequest(id, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(professorLoginRequest);
    }

    public void login_student(){
        String id = et_id.getText().toString();
        String password = et_pass.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("harang" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 로그인에 성공한 경우
                        String user_id = jsonObject.getString("id");
                        String user_pass = jsonObject.getString("password");
                        String user_primaryKey = jsonObject.getString("s_id");
                        String user_name = jsonObject.getString("s_name");

                        Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("user_pass", user_pass);
                        intent.putExtra("s_id", user_primaryKey);
                        intent.putExtra("s_name", user_name);

                        startActivity(intent);
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StudentLoginRequest studentLoginRequest = new StudentLoginRequest(id, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(studentLoginRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressedEvent.onBackPressed();
    }
}