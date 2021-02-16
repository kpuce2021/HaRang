package com.example.harang;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class AccountLoginActivity extends AppCompatActivity {
    private static final String TAG = "heejeong";
    private CognitoUserPool userPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        final EditText editId = findViewById(R.id.login_edit_id);
        final EditText editPw = findViewById(R.id.login_edit_pw);

        Button btnSignIn = findViewById(R.id.login_btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountLoginActivity.this, MainActivity.class);
                startActivity(intent);
                Log.i(TAG,"start main");
                finish();
                Log.i(TAG,"finish account failure");
            }
        });

        Button btnSignUp = findViewById(R.id.login_btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountLoginActivity.this, AccountSignupActivity.class);
                startActivity(intent);
                Log.i(TAG,"start signUp");
                finish();
                Log.i(TAG,"finish account failure");
            }
        });

    }

}
