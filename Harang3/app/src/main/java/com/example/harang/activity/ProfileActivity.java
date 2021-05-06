package com.example.harang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.harang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private TextView tv_total, tv_seperate;
    private ProgressBar pb_total, pb_seperate;
    private static String IP = "3.214.234.24"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_total = findViewById(R.id.tv_total);
        tv_seperate = findViewById(R.id.tv_seperate);
        pb_total = findViewById(R.id.pb_total);
        pb_seperate = findViewById(R.id.pb_seperate);

        String url = "http://" + IP + "/profile.php";
        selectDatabase selectDatabase = new selectDatabase(url, null);
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
           // String result = "";
            String total = "";
            String seperate = "";
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data_concentrate");

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject output = jsonArray.getJSONObject(i);

                total += output.getString("c_total");
                seperate += output.getString("c_seperate");
               /* result += output.getString("e_id")
                        + " / "
                        + output.getString("v_id")
                        + " / "
                        + output.getString("s_id")
                        + " / "
                        + output.getString("d_time")
                        + " / "
                        + output.getString("concentration")
                        + " / "
                        + output.getString("e_status")
                        + " / "
                        + output.getString("mstate")
                        + "\n";*/


            }
            pb_total.setProgress(Integer.parseInt(total));
            pb_seperate.setProgress(Integer.parseInt(seperate));
        /*    txtView = findViewById(R.id.txtView);
            txtView.setText(result);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}