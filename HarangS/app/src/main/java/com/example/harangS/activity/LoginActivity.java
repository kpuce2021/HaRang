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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.harangS.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText et_id, et_pass;
    private Button btn_login, btn_register;
    private BackPressedEvent backPressedEvent;


    private static String user_id;
    private static String user_pass;
    private static String user_primaryKey;
    private static String user_name;
    private static boolean success;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        backPressedEvent = new BackPressedEvent(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 키보드가 focus된 edittext 밑으로 옴

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
                login_student();
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

    public void login_student(){
        String id = et_id.getText().toString();
        String password = et_pass.getText().toString();

        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                success = jsonObject.getBoolean("success");
                if (success) { // 로그인에 성공한 경우
                    user_id = jsonObject.getString("id");
                    user_pass = jsonObject.getString("password");
                    user_primaryKey = jsonObject.getString("s_id");
                    user_name = jsonObject.getString("s_name");
                    Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                    Log.i("db_test","login success");

                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("user_pass", user_pass);
                    intent.putExtra("s_id", user_primaryKey);
                    intent.putExtra("s_name", user_name);

                    startActivity(intent);
                } else { // 로그인에 실패한 경우
                    Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    Log.i("db_test","login false");
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("db_test","login error : "+e);
            }
        };
        StudentLoginRequest studentLoginRequest = new StudentLoginRequest(id, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(studentLoginRequest);
    }

    @Override
    public void onBackPressed() {
        backPressedEvent.onBackPressed();
    }
}