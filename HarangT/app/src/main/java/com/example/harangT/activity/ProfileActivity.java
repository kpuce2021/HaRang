package com.example.harangT.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.harangT.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private TextView tv_total, tv_seperate, tv_total_percent, tv_seperate_percent;
    private ProgressBar pb_total, pb_seperate;
    private static String IP = "100.26.4.92"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_total = findViewById(R.id.tv_total);
        tv_seperate = findViewById(R.id.tv_seperate);
        pb_total = findViewById(R.id.pb_total);
        pb_seperate = findViewById(R.id.pb_seperate);
        tv_total_percent = findViewById(R.id.tv_total_percent);
        tv_seperate_percent = findViewById(R.id.tv_seperate_percent);

        String url = "http://" + IP + "/profileTest.php";
        selectDatabase selectDatabase = new selectDatabase(url, null);
        selectDatabase.execute();
    }

    class selectDatabase extends AsyncTask<Void, Void, String> {

        private String url1;
        private ContentValues values1;
        String result1; // 요청 결과를 저장할 변수.

        public selectDatabase(String url, ContentValues contentValues) {
            this.url1 = url;
            this.values1 = contentValues;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result1 = requestHttpURLConnection.request(url1, values1); // 해당 URL로 부터 결과물을 얻어온다.
            return result1; // 여기서 당장 실행 X, onPostExcute에서 실행
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            doJSONParser(s); // 파서로 전체 출력
        }
    }

    // 받아온 json 데이터를 파싱합니다.
    public void doJSONParser(String string) {
        try {
            int total = 0;
            int seperate = 0;
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data_concentrate");

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject output = jsonArray.getJSONObject(i);
                total += output.getInt("c_total");
                seperate += output.getInt("c_seperate");
            }
            if(jsonArray.length() != 0){
                total /= jsonArray.length();
                seperate /= jsonArray.length();
                pb_total.setProgress(total);
                pb_seperate.setProgress(seperate);
                tv_total_percent.setText(Integer.toString(total));
                tv_seperate_percent.setText(Integer.toString(seperate));
            } else{
                pb_total.setProgress(0);
                pb_seperate.setProgress(0);
                tv_total_percent.setText("0");
                tv_seperate_percent.setText("0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}