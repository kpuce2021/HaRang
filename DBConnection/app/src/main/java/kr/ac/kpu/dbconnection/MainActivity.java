package kr.ac.kpu.dbconnection;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import kr.ac.kpu.dbconnection.R;
import kr.ac.kpu.dbconnection.RequestHttpURLConnection;

public class MainActivity extends AppCompatActivity {

    private TextView txtView;
    private EditText editText1, editText2, editText3, editText4, editText5, editText6, editText7;
    Button insertBtn;

   // private static String IP = "ec2-13-125-182-211.ap-northeast-2.compute.amazonaws.com"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    private static String IP = "15.164.145.147"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 사용할 액티비티 선언
        editText1 = findViewById(R.id.edtText1);
        editText2 = findViewById(R.id.edtText2);
        editText3 = findViewById(R.id.edtText3);
        editText4 = findViewById(R.id.edtText4);
        editText5 = findViewById(R.id.edtText5);
        editText6 = findViewById(R.id.edtText6);
        editText7 = findViewById(R.id.edtText7);

        insertBtn = findViewById(R.id.insertBtn);

        // String url = "http://" + IP + "/php파일명.php";
        String url = "http://" + IP + "/connect.php";
        selectDatabase selectDatabase = new selectDatabase(url, null);
        /*insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectDatabase.execute(); // AsyncTask는 .excute()로 실행된다.
            }
        });*/
        selectDatabase.execute(); // AsyncTask는 .excute()로 실행된다.

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertoToDatabase(editText1.getText().toString(),editText2.getText().toString(),editText3.getText().toString(),editText4.getText().toString(),editText5.getText().toString(),editText6.getText().toString(),editText7.getText().toString());
            }
        });
    }
    private void insertoToDatabase(final String ed1, String ed2, String ed3, String ed4, String ed5, String ed6, String ed7) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Log.d("Tag : ", s); // php에서 가져온 값을 최종 출력함
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String edt1Text = (String) params[0];
                    String edt2Text = (String) params[1];
                    String edt3Text = (String) params[2];
                    String edt4Text = (String) params[3];
                    String edt5Text = (String) params[4];
                    String edt6Text = (String) params[5];
                    String edt7Text = (String) params[6];


                    String link = "http://"+IP+"/insert.php";
                    String data = URLEncoder.encode("e_id", "UTF-8") + "=" + URLEncoder.encode(edt1Text, "UTF-8");
                    data += "&" + URLEncoder.encode("v_id", "UTF-8") + "=" + URLEncoder.encode(edt2Text, "UTF-8");
                    data += "&" + URLEncoder.encode("s_id", "UTF-8") + "=" + URLEncoder.encode(edt3Text, "UTF-8");
                    data += "&" + URLEncoder.encode("d_time", "UTF-8") + "=" + URLEncoder.encode(edt4Text, "UTF-8");
                    data += "&" + URLEncoder.encode("concentration", "UTF-8") + "=" + URLEncoder.encode(edt5Text, "UTF-8");
                    data += "&" + URLEncoder.encode("e_status", "UTF-8") + "=" + URLEncoder.encode(edt6Text, "UTF-8");
                    data += "&" + URLEncoder.encode("mstate", "UTF-8") + "=" + URLEncoder.encode(edt7Text, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                    outputStreamWriter.write(data);
                    outputStreamWriter.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("tag : ", sb.toString()); // php에서 결과값을 리턴
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(ed1,ed2,ed3,ed4,ed5,ed6,ed7);
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
//            txtView.setText(s); // 파서 없이 전체 출력
            doJSONParser(s); // 파서로 전체 출력
        }
    }

    // 받아온 json 데이터를 파싱합니다.
    public void doJSONParser(String string) {
        try {
            String result = "";
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data_eyetrack");

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject output = jsonArray.getJSONObject(i);
                result += output.getString("e_id")
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
                        + "\n";
            }

            txtView = findViewById(R.id.txtView);
            txtView.setText(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}