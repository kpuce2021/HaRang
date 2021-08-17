package com.example.harangT.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangT.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment3 extends Fragment {
    public ProfessorFragment3(){ }
    public static ProfessorFragment3 newInstance() {
        return new ProfessorFragment3();
    }
    private View view;

    private static Context mContext;
    private static Activity mActivity;

    private static String ProfessorId;
    private static String p_id;
    private static ArrayList<HashMap<String,String>> studentMap;
    private static HashMap<String,String> studentItems;
    int studentCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment3, container, false);

        Button btn_modify = view.findViewById(R.id.btn_modify);
        Button btn_refresh = view.findViewById(R.id.btn_refresh);
        Button btn_delete = view.findViewById(R.id.btn_delete);
        //id 받아오기
        ProfessorId = p_BaseActivity.ProfessorId;
        p_id = p_BaseActivity.p_id;

        //목록 불러오기
        accessDB(p_id);

        //새로고침 버튼
        btn_refresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                accessDB(p_id);
            }
        });

        //수정 액태비티 띄우기
        btn_modify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), PEnrollModifyActivity.class);
                startActivity(intent);
            }
        });


        //삭제 버튼 띄우기
        btn_delete.setOnClickListener(new OnSingleClickListener() {
            @Override public void onSingleClick(View v) {
                deleteUI();
            }
        });
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }

        super.onAttach(context);
    }


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

                            /*
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.attach(ProfessorFragment3.this);
                            ft.commit();*/
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
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(pEnrollmentReadRequest);


        //로딩용 딜레이 필요함


    }

    private void initUI(){
        //studentItems
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TableRow tableRow;

        //초기화 후 다시 새로고침
        int childCount = tableLayout.getChildCount();
        tableLayout.removeViews(1, childCount - 1);

        TextView[] tv = new TextView[3];
        Button[] btnchild = new Button[studentCount];

        for(int i=0;i<studentCount;i++){
            tableRow = new TableRow(mContext);

            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            for(int j=0;j<3;j++){
                tv[j] = new TextView(mContext);
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

            btnchild[i] = new Button(mContext);
            btnchild[i].setText("삭제");
            int finalI = i;
            btnchild[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //map에서 i번째 s_id 넘겨줌.
                    deleteEnrollmentDB(studentMap.get(finalI).get("s_id"));
                    accessDB(p_id);
                }
            });
            btnchild[i].setVisibility(View.INVISIBLE);
            //btnchild[i].setBackgroundResource(R.drawable.button_round);
            btnchild[i].setGravity(Gravity.CENTER);
            btnchild[i].setTextSize(15);
            btnchild[i].setTextColor(Color.BLACK);
            btnchild[i].setHeight(tv[0].getHeight());
            tableRow.addView(btnchild[i]);


            tableLayout.addView(tableRow);
        }

    }

    private void deleteUI(){
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TableRow titleRow = (TableRow)tableLayout.getChildAt(0);
        TextView titleTextView = (TextView) titleRow.getChildAt(3);
        titleTextView.setVisibility(View.VISIBLE);

        int rowSize = tableLayout.getChildCount();
        Button innerButton;
        TableRow innerRow;
        for(int i=1;i<rowSize;i++){
            innerRow = (TableRow)tableLayout.getChildAt(i);
            innerButton = (Button) innerRow.getChildAt(3);
            innerButton.setVisibility(View.VISIBLE);
        }

    }


    private void deleteEnrollmentDB(String s_id){
        //table 값 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.i("db_test", "delete success");
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
        PEnrollmentDelRequest pEnrollmentDelRequest = new PEnrollmentDelRequest(p_id, s_id, responseListener);
        pEnrollmentDelRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(pEnrollmentDelRequest);


        //로딩용 딜레이 필요함


    }

}