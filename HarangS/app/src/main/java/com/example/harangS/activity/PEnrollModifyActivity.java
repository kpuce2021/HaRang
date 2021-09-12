package com.example.harangS.activity;

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
import com.example.harangS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PEnrollModifyActivity extends AppCompatActivity {
    private static String ProfessorId;
    private static String p_id;

    private static ArrayList<HashMap<String,String>> studentMap;
    private static HashMap<String,String> studentItems;
    int studentCount = -1;
    CheckBox[] checkchild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_activity_enrollmodify);

        //id 받아오기
        ProfessorId = p_BaseActivity.ProfessorId;
        p_id = p_BaseActivity.p_id;

        Button btn_search = findViewById(R.id.btn_search);
        Button btn_refresh = findViewById(R.id.btn_refresh);
        Button btn_save = findViewById(R.id.btn_save);
        Spinner spinner = findViewById(R.id.spinner);
        EditText editText = findViewById(R.id.editText);

        accessDB(p_id);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 스피너, editText 정보 받아오기
                String spinnerString = spinner.getSelectedItem().toString();

                if(spinnerString.equals("이름")){
                    searchDB(p_id,"s_name",editText.getText().toString());
                }else if(spinnerString.equals("아이디")){
                    searchDB(p_id,"id",editText.getText().toString());
                }


                //새로고침
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessDB(p_id);

                //새로고침
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkchild == null || studentCount == -1){//목록이 안뜬 상태에서 저장 눌렀을 경우
                    finish();  //액티비티 종료
                }

                TableLayout tableLayout = findViewById(R.id.modifyTableLayout);
                TableRow tableRow;

                if(studentCount>=0){//정상적으로 저장 눌렀을 때
                    ArrayList<String> checkList = new ArrayList<>(); //s_id 목록 저장
                    for(int i=0;i<studentCount;i++){ //체크박스 탐색
                        if(checkchild[i].isChecked()){
                            //검색 후 목록은 인덱스 순서가 아니라서 error 발생
                            TableRow titleRow = (TableRow)tableLayout.getChildAt(i+1);
                            TextView sidTv = (TextView) titleRow.getChildAt(0); //s_id 번호 : 5

                            checkList.add(sidTv.getText().toString());
                        }
                    }
                    updateDB(p_id, checkList);//수강신청 db 업로드
                    finish();//액티비티 종료
                }
            }
        });
    }






    private void initUI(){
//studentItems
        if(studentCount == -1){
            return;
        }
        TableLayout tableLayout = findViewById(R.id.modifyTableLayout);
        TableRow tableRow;

        //초기화 후 다시 새로고침
        int childCount = tableLayout.getChildCount();
        tableLayout.removeViews(1, childCount - 1);

        TextView[] tv = new TextView[3];

        checkchild = new CheckBox[studentCount];

        for(int i=0;i<studentCount;i++){
            tableRow = new TableRow(this);

            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            for(int j=0;j<3;j++){
                tv[j] = new TextView(this);
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

            //체크박스
            checkchild[i] = new CheckBox(this);
            checkchild[i].setChecked(false);
            tableRow.addView(checkchild[i]);

            tableLayout.addView(tableRow);
        }
    }




    //DB 연결
    //학생 추가
    private void updateDB(String p_id, ArrayList<String> checkList) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.i("db_test", "update success");
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
        PEnrollmentUpdateRequest pEnrollmentUpdateRequest = new PEnrollmentUpdateRequest(p_id, checkList, responseListener);
        pEnrollmentUpdateRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pEnrollmentUpdateRequest);


        //로딩용 딜레이 필요함


    }

    //목록 읽어오기
    private void accessDB(String p_id){
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
        PEnrollmentNReadRequest pEnrollmentNReadRequest = new PEnrollmentNReadRequest(p_id, responseListener);
        pEnrollmentNReadRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pEnrollmentNReadRequest);


        //로딩용 딜레이 필요함


    }

    //검색
    private void searchDB(String p_id, String flag, String searchString){
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
        PEnrollmentSearchRequest pEnrollmentSearchRequest = new PEnrollmentSearchRequest(p_id,flag, searchString, responseListener);
        pEnrollmentSearchRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pEnrollmentSearchRequest);


        //로딩용 딜레이 필요함


    }



}