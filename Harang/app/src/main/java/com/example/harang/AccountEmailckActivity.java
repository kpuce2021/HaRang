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

public class AccountEmailckActivity extends AppCompatActivity {
    private static final String TAG = "heejeong";
    private CognitoUserPool userPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_emailck);

        Intent intent = getIntent();
        String signupId = intent.getStringExtra("USER_ID");



        EditText editId = findViewById(R.id.emailck_edit_id);
        editId.setText(signupId);

        final EditText editCode = findViewById(R.id.emailck_edit_code);

        Button btncomfirm = findViewById(R.id.emailck_btn_comfirm);
        btncomfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmTask().execute(String.valueOf(editCode.getText()),String.valueOf(editId.getText()));

                Intent intent = new Intent(AccountEmailckActivity.this, AccountLoginActivity.class);
                startActivity(intent);
                Log.i(TAG,"start Login");
                finish();
                Log.i(TAG,"finish Emailck");
            }
        });

    }

    private class ConfirmTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            final String[] result = new String[1];

            //Callback handler for confirmSignUp API
            final GenericHandler confirmationCallback = new GenericHandler() {
                @Override
                public void onSuccess() {
                    //User was successfully confirmed
                    result[0] = "Successeded!";


                }

                @Override
                public void onFailure(Exception exception) {
                    result[0] = "Failed!"+exception.getMessage();
                }
            };

            CognitoSettings cognitoSettings = new CognitoSettings(AccountEmailckActivity.this);

            CognitoUser thisUser = cognitoSettings.getUserPool().getUser(strings[1]);
            thisUser.confirmSignUp(strings[0],false,confirmationCallback);

            return result[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i(TAG,"Confirmation result: "+result);
        }
    }

}
