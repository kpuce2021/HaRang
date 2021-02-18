package com.example.harang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class CpptestActivity extends AppCompatActivity {
    private static final String TAG = "heejeong";
    private CognitoUserPool userPool;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpptest);
        Intent intent = getIntent();
        String sessionId = intent.getStringExtra("sessionId");
        String sessionPw = intent.getStringExtra("sessionPw");

        Log.i(TAG,"user session : "+sessionId+"   "+sessionPw);


        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        TextView textUserid = findViewById(R.id.mainTextUserid);
        TextView textSession = findViewById(R.id.mainTextSession);
        textSession.setText(sessionId+"  "+sessionPw);


        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                textUserid.setText(userSession.getUsername()+"님 환영합니다.");
            }
            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId,String.valueOf(sessionPw),null);
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                authenticationContinuation.continueTask();
            }
            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) { }
            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) { }
            @Override
            public void onFailure(Exception exception) { textUserid.setText("로그인 실패"); }
        };

        CognitoSettings cognitoSettings = new CognitoSettings(CpptestActivity.this);
        CognitoUser thisUser = cognitoSettings.getUserPool().getUser(String.valueOf(sessionId));

        thisUser.getSessionInBackground(authenticationHandler); //백그라운드 동작

        tv.setText(stringFromJNI());


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}