package com.example.harang;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;

public class AccountSignupActivity extends AppCompatActivity {
    private static final String TAG = "heejeong";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_signup);

        registerUser();


    }

    private void registerUser() {
        final EditText editNickname = findViewById(R.id.signup_edit_name);
        final EditText editEmail = findViewById(R.id.signup_edit_email);
        final EditText editId = findViewById(R.id.signup_edit_id);
        final EditText editPw = findViewById(R.id.signup_edit_pw);

        //Create a CognitoUserAttributes object and add user attributes
        final CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        final SignUpHandler signupCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                //signup is successfull
                Log.i(TAG,"signup success...is confiremed "+signUpResult);
                //check if this user needs to be confirmed
                if(!signUpResult.isUserConfirmed()){
                    Log.i(TAG,"signup success...not confiremed ");
                }else{
                    Log.i(TAG,"signup success");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                //signup failed, ckeck exception for the cause
                Log.i(TAG,"signup failure "+exception.getLocalizedMessage());
            }
        };

        Button btnRegister = findViewById(R.id.signup_btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAttributes.addAttribute("nickname",String.valueOf(editNickname.getText()));
                userAttributes.addAttribute("email",String.valueOf(editEmail.getText()));

                CognitoSettings cognitoSettings = new CognitoSettings(AccountSignupActivity.this);
                //signupinbackgroung - 메인 스레드 밖에서 실행
                cognitoSettings.getUserPool().signUpInBackground(String.valueOf(editId.getText()),String.valueOf(editPw.getText()),userAttributes,null,signupCallback);

                Intent intent = new Intent(AccountSignupActivity.this, AccountEmailckActivity.class);
                intent.putExtra("USER_ID",editId.getText());
                startActivity(intent);
                Log.i(TAG,"start emailck");
                //finish();
            }
        });
    }

}
