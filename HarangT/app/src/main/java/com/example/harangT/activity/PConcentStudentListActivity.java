package com.example.harangT.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangT.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PConcentStudentListActivity extends AppCompatActivity {
    private static String ProfessorId;
    private static String p_id;


    private static String type;
    private static String v_id;
    private static String v_name;


    private static ArrayList<HashMap<String,String>> studentMap;
    private static HashMap<String,String> studentItems;
    int studentCount = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_concentstudent);

        //id 받아오기
        ProfessorId = p_BaseActivity.ProfessorId;
        p_id = p_BaseActivity.p_id;


        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        v_id = intent.getStringExtra("v_id");
        v_name = intent.getStringExtra("v_name");

        accessDB(p_id);

    }



    //DB 연결
    //목록 읽어오기
    private void accessDB(final String p_id){
        //table 값 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {

                        studentMap = new ArrayList<HashMap<String, String>>();

                        studentCount = Integer.parseInt(jsonObject.getString("count"));
                        for(int i=0;i<studentCount;i++){
                            studentItems = new HashMap<>();
                            JSONObject output = jsonObject.getJSONObject(String.valueOf(i));
                            Log.i("db_test", " s_id : " + output.getString("s_id"));
                            Log.i("db_test", " id : " + output.getString("id"));
                            Log.i("db_test", " s_name : " + output.getString("s_name"));
                            Log.i("db_test", " ");

                            studentItems.put("s_id",output.getString("s_id"));
                            studentItems.put("id",output.getString("id"));
                            studentItems.put("s_name",output.getString("s_name"));

                            studentMap.add(studentItems);

                        }
                        initUI();
                    } else {
                        Log.i("db_test", "server connect fail");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("db_test", "catch : " + e.getMessage());
                }

            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        PEnrollmentReadRequest pEnrollmentReadRequest = new PEnrollmentReadRequest(p_id, responseListener);
        pEnrollmentReadRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(pEnrollmentReadRequest);


        //로딩용 딜레이 필요함


    }


    private void initUI(){
        //studentItems
        TableLayout tableLayout = findViewById(R.id.stuListtableLayout);
        TableRow tableRow;

        //초기화 후 다시 새로고침
        int childCount = tableLayout.getChildCount();
        tableLayout.removeViews(1, childCount - 1);

        TextView[] tv = new TextView[3];
        Button[] btnchild = new Button[studentCount];

        for(int i=0;i<studentCount;i++){
            tableRow = new TableRow(getApplicationContext());

            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            for(int j=0;j<3;j++){
                tv[j] = new TextView(getApplicationContext());
                //tv[j].setBackgroundResource(R.drawable.table_inside);
                tv[j].setGravity(Gravity.CENTER);
                tv[j].setTextSize(15);
                tv[j].setTextColor(Color.BLACK);
            }

            tv[0].setText(studentMap.get(i).get("s_id"));
            tableRow.addView(tv[0]);

            tv[1].setText(studentMap.get(i).get("id"));
            tableRow.addView(tv[1]);

            tv[2].setText(studentMap.get(i).get("s_name"));
            tableRow.addView(tv[2]);

            final int index = i;
            tableRow.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), PConStuPersonalActivity.class);
                intent.putExtra("s_id",studentMap.get(index).get("s_id"));
                intent.putExtra("StudentId",studentMap.get(index).get("id"));
                intent.putExtra("s_name",studentMap.get(index).get("s_name"));

                intent.putExtra("type",type);
                intent.putExtra("v_id",v_id);
                intent.putExtra("v_name",v_name);

                startActivity(intent);
            });

            tableLayout.addView(tableRow);

        }

    }


}