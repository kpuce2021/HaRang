package com.example.harangS.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_name, et_pass, et_passConfirm, et_major;
    private Button btn_register, btn_validation;
    private RadioButton rb_professor, rb_student;
    private RadioGroup rg_register;
    private int val_flag = 0;
    private int pass_flag = 0;
    private AlertDialog dialog;
    private boolean validation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 시작시 처음으로 실행되는 생명주기!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 아이디 값 찾아주기
        et_id = findViewById(R.id.et_id);
        et_name = findViewById(R.id.et_name);
        et_pass = findViewById(R.id.et_pass);
        et_passConfirm = findViewById(R.id.et_passConfirm);
        et_major = findViewById(R.id.et_major);
        rb_professor = findViewById(R.id.rb_professor);
        rb_student = findViewById(R.id.rb_student);
        rg_register = findViewById(R.id.rg_register);

        btn_register = findViewById(R.id.btn_register);
        btn_validation = findViewById(R.id.btn_validation);

        et_major.setVisibility(View.VISIBLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 키보드가 focus된 edittext 밑으로 옴

        rg_register.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_student){
                    et_major.setVisibility(View.INVISIBLE);
                    rb_student.setChecked(true);
                }else{
                    et_major.setVisibility(View.VISIBLE);
                    rb_professor.setChecked(true);
                }
            }
        });



        // 중복확인 버튼 클릭 시 수행
        btn_validation.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (rb_professor.isChecked()) {     // 교수가 아이디 중복확인을 하는 경우
                    check_professor_duplication();
                }

                if (rb_student.isChecked()) {      // 학생이 아이디 중복확인을 하는 경우
                    check_student_duplication();
                }
            }
        });
        /*btn_validation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (rb_professor.isChecked()) {     // 교수가 아이디 중복확인을 하는 경우
                    check_professor_duplication();
                }

                if (rb_student.isChecked()) {      // 학생이 아이디 중복확인을 하는 경우
                    check_student_duplication();
                }
            }
        });*/

        // 회원가입 버튼 클릭 시 수행
        btn_register.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // EditText에 현재 입력되어있는 값을 get(가져온다)해온다.

                if (rb_professor.isChecked()) {     // 교수가 회원가입을 하는 경우
                    register_professor();
                }

                if (rb_student.isChecked()) {       // 학생이 회원가입을 하는 경우
                    register_student();
                }
            }
        });
      /*  btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get(가져온다)해온다.

                if (rb_professor.isChecked()) {     // 교수가 회원가입을 하는 경우
                    register_professor();
                }

                if (rb_student.isChecked()) {       // 학생이 회원가입을 하는 경우
                    register_student();
                }
            }
        });*/

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {     // edittext 작성 도중 edittext가 아닌 밖 터치 시 키보드 사라짐
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

    public int check_password(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
        String alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(symbol);
        Pattern pattern_alpha = Pattern.compile(alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (matcher_symbol.find() && matcher_alpha.find()) {
            pass_flag = 1;
            return pass_flag;
        }else if(0 < password.length() && password.length() < 8){
            Toast.makeText(this, "비밀번호를 8자리 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return 0;

        }else {
            Toast.makeText(this, "비밀번호는 영문, 숫자, 특수문자가 포함되어야 합니다.", Toast.LENGTH_SHORT).show();
            return 0;
        }

    }

    public int check_professor_validation(String id, String password, String p_name, String p_major, String passwordConfirm) {
        if (id.length() == 0) {
            Toast.makeText(RegisterActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_id.requestFocus();
            return 0;
        }

        if (password.length() == 0) {
            Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_pass.requestFocus();
            return 0;
        }

        if (p_name.length() == 0) {
            Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            et_name.requestFocus();
            return 0;
        }

        if (passwordConfirm.length() == 0) {
            Toast.makeText(RegisterActivity.this, "재확인 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_passConfirm.requestFocus();
            return 0;
        }

        if (p_major.length() == 0) {
            Toast.makeText(RegisterActivity.this, "과목을 입력하세요.", Toast.LENGTH_SHORT).show();
            et_name.requestFocus();
            return 0;
        }

        if (!passwordConfirm.equals(password)) {
            Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            et_passConfirm.setText("");
            et_passConfirm.requestFocus();
        }
        else {
            val_flag = 1;
            return val_flag;
        }
        return 0;
    }

    public int check_student_validation(String id, String password, String s_name, String passwordConfirm) {
        if (id.length() == 0) {
            Toast.makeText(RegisterActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_id.requestFocus();
            return 0;
        }

        if (password.length() == 0) {
            Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_pass.requestFocus();
            return 0;
        }

        if (s_name.length() == 0) {
            Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            et_name.requestFocus();
            return 0;
        }

        if (passwordConfirm.length() == 0) {
            Toast.makeText(RegisterActivity.this, "재확인 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            et_passConfirm.requestFocus();
            return 0;
        }

        if (!passwordConfirm.equals(password)) {
            Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            et_passConfirm.setText("");
            et_passConfirm.requestFocus();
        } else {
            val_flag = 1;
            return val_flag;
        }
        return 0;
    }

    public void check_professor_duplication(){
        String id = et_id.getText().toString();
        if(validation)
        {
            return;
        }
        if(id.length() == 0){
            AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
            dialog=builder.setMessage("아이디를 입력해주세요.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            return;
        }
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                        dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        et_id.setEnabled(false);
                        validation = true;
                        btn_validation.setText("확인");
                    }
                    else{
                        AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                        dialog=builder.setMessage("중복된 아이디입니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ProfessorValidationRequest professorValidateRequest=new ProfessorValidationRequest(id,responseListener);
        RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
        queue.add(professorValidateRequest);

    }

    public void check_student_duplication(){
        String id = et_id.getText().toString();
        if(validation)
        {
            return;
        }
        if(id.length() == 0){
            AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
            dialog=builder.setMessage("아이디를 입력해주세요.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            return;
        }
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                        dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        et_id.setEnabled(false);
                        validation = true;
                        btn_validation.setText("확인");
                    }
                    else{
                        AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                        dialog=builder.setMessage("중복된 아이디입니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StudentValidationRequest studentValidateRequest=new StudentValidationRequest(id,responseListener);
        RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
        queue.add(studentValidateRequest);

    }

    public void register_professor(){
        String id = et_id.getText().toString();
        String p_name = et_name.getText().toString();
        String password = et_pass.getText().toString();
        String passwordConfirm = et_passConfirm.getText().toString();
        String p_major = et_major.getText().toString();


        //int userAge = Integer.parseInt(et_age.getText().toString());
        check_professor_validation(id, password, p_name, p_major, passwordConfirm);
        check_password(password);


        if (val_flag == 1 && pass_flag == 1) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { // 회원등록에 성공한 경우
                            Toast.makeText(getApplicationContext(), "회원 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else { // 회원등록에 실패한 경우
                            Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            // 서버로 Volley를 이용해서 요청을 함.
            ProfessorRegisterRequest professorRegisterRequest = new ProfessorRegisterRequest(id, password, p_name, p_major, responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(professorRegisterRequest);
        }
        val_flag = 0;
        pass_flag = 0;
    }

    public void register_student(){
        String id = et_id.getText().toString();
        String s_name = et_name.getText().toString();
        String password = et_pass.getText().toString();
        String passwordConfirm = et_passConfirm.getText().toString();


        //int userAge = Integer.parseInt(et_age.getText().toString());
        check_student_validation(id, password, s_name, passwordConfirm);
        check_password(password);


        if (val_flag == 1 && pass_flag == 1) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { // 회원등록에 성공한 경우
                            Toast.makeText(getApplicationContext(), "회원 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else { // 회원등록에 실패한 경우
                            Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            // 서버로 Volley를 이용해서 요청을 함.
            StudentRegisterRequest studentRegisterRequest = new StudentRegisterRequest(id, password, s_name, responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(studentRegisterRequest);
        }
        val_flag = 0;
        pass_flag = 0;
    }
}