package com.example.harang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class AccountLoginActivity extends AppCompatActivity {
    private static final String TAG = "heejeong";
    private CognitoUserPool userPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        final EditText editId = findViewById(R.id.login_edit_id);
        final EditText editPw = findViewById(R.id.login_edit_pw);

        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.i(TAG,"login successful. can get tokens here");
/*
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(userSession);
                Log.i(TAG,"user session : "+json);
*/
                Intent intent = new Intent(AccountLoginActivity.this, CpptestActivity.class);
                intent.putExtra("sessionId",editId.getText().toString());
                intent.putExtra("sessionPw",editPw.getText().toString());
                startActivity(intent);
                //finish();
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.i(TAG,"in getAuthenticationDetails()");

                //id, pw 얻어오기
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId,String.valueOf(editPw.getText()),null);

                //pass the user signin credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                //allow the signin to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.i(TAG,"in getMFACode()");
                //if multifactor authentication is required, get the verification code from user
                // multiFactoAuthenticationContinuation.setNfaCode(mfaVerificationCode);

                //allow the signin process to continue
                // multiFactorAuthenticationContinuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.i(TAG,"in authenticationChallenge()");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG,"loginfailed "+exception.getLocalizedMessage());
            }
        };

        Button btnSignIn = findViewById(R.id.login_btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CognitoSettings cognitoSettings = new CognitoSettings(AccountLoginActivity.this);

                CognitoUser thisUser = cognitoSettings.getUserPool().getUser(String.valueOf(editId.getText()));
                Log.i(TAG,"in button clicked...");

                thisUser.getSessionInBackground(authenticationHandler); //백그라운드 동작

                //Intent intent = new Intent(AccountLoginActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
            }
        });

        Button btnSignUp = findViewById(R.id.login_btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountLoginActivity.this, AccountSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
