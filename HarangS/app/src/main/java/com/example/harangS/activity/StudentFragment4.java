package com.example.harangS.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentFragment4 extends Fragment {
    public static StudentFragment4 newInstance() {
        return new StudentFragment4();
    }
    private View view;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    public static ArrayList<HashMap<String,String>> menuItemsInfo; //영상들의 정보를 담는 리스트
    private static HashMap<String,String> menuItem;

    private static int listCount = 0;
    private static VideoListViewAdapter adapter; //리스트 어답터
    private static String StudentId;
    private static String s_id;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_fragment4, container, false);

        //items 로드
        StudentId = BaseActivity.StudentId;
        s_id = BaseActivity.s_id;



        accessDB(StudentId);

        Button eyetracking = view.findViewById(R.id.eyetrackingS);
        eyetracking.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(mContext, EyetrackingActivity.class);
                intent.putExtra("user_id",BaseActivity.StudentId);
                intent.putExtra("s_id",BaseActivity.s_id);
                startActivity(intent);
            }
        });



        Button startRTMP = view.findViewById(R.id.startRTMP);
        startRTMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StudentStreamRTMPActivity.class);
                startActivity(intent);
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







    //accdssDB
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accessDB(final String studentId){
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    menuItemsInfo = new ArrayList<>();

                    listCount = Integer.parseInt(jsonObject.getString("count"));
                    Log.i("db_test","listCount : "+listCount);
                    for(int i=0;i<listCount;i++){
                        menuItem = new HashMap<>();
                        JSONObject output = jsonObject.getJSONObject(String.valueOf(i));

                        Log.i("db_test", " streamId : " + output.getString("streamId"));
                        Log.i("db_test", " p_id : " + output.getString("p_id"));
                        Log.i("db_test", " p_name : " + output.getString("p_name"));
                        Log.i("db_test", " videoName : " + output.getString("videoName"));
                        Log.i("db_test", " startTime : " + output.getString("startTime"));
                        Log.i("db_test", " type : " + output.getString("type"));
                        Log.i("db_test", " ");


                        menuItem.put("streamId",output.getString("streamId"));
                        menuItem.put("p_id",output.getString("p_id"));
                        menuItem.put("p_name",output.getString("p_name"));
                        menuItem.put("videoName",output.getString("videoName"));
                        menuItem.put("startTime",output.getString("startTime"));
                        menuItem.put("type",output.getString("type"));

                        menuItemsInfo.add(menuItem);

                        //List에 넣고 화면에 띄우기

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

        };
        // 서버로 Volley를 이용해서 요청을 함.
        StudentStreamListRequset request = new StudentStreamListRequset(s_id, responseListener);
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);


        //로딩용 딜레이 필요함

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void initUI() {

        LinearLayout parentLinear = view.findViewById(R.id.streamVideoList);
        parentLinear.removeAllViews();

        TextView[] tv = new TextView[5];
        for(int i=0;i<listCount;i++) {
            //사진,설명 담아놓은 가장 바깥 뷰  //weight 설정 없음
            LinearLayout outlinearLayout = new LinearLayout(mContext);
            outlinearLayout.setBackgroundResource(R.drawable.border_layout);
            outlinearLayout.setWeightSum(3);

//            //썸네일
//            ImageView imageview = new ImageView(mContext);
//            imageview.setImageResource(R.drawable.img1);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    200,
//                    300
//            );
//            params.setMargins(40,30,50,30);
//            imageview.setLayoutParams(params);
//            outlinearLayout.addView(imageview);

            //텍스트 영역
            LinearLayout inlinearLayout = new LinearLayout(mContext);//weight 설정 없음
            //inlinearLayout.setBackgroundResource(R.drawable.border_layout);

            inlinearLayout.setGravity(Gravity.CENTER);
            inlinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            inlinearLayout.setPaddingRelative(0,50,0,0);
            inlinearLayout.setOrientation(LinearLayout.VERTICAL);

            //textview
            for(int j=0;j<4;j++){
                tv[j] = new TextView(mContext);
                tv[j].setTextSize(20);
                tv[j].setTextColor(Color.BLACK);


                Typeface typeface = getResources().getFont(R.font.font);
                tv[j].setTypeface(typeface);

            }

            tv[0].setText(menuItemsInfo.get(i).get("p_name"));
            inlinearLayout.addView(tv[0]);
            tv[1].setText("강의명 : " + menuItemsInfo.get(i).get("videoName"));
            inlinearLayout.addView(tv[1]);
            tv[2].setText("시작 시간 : "+menuItemsInfo.get(i).get("startTime"));
            inlinearLayout.addView(tv[2]);
            tv[3].setText("강의 타입 : "+(menuItemsInfo.get(i).get("type").equals("stream")?"실시간 강의":"녹화강의"));
            inlinearLayout.addView(tv[3]);

            outlinearLayout.addView(inlinearLayout);
            final int index = i;
            outlinearLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, StudentStreamRTMPActivity.class);
                intent.putExtra("url", "rtmp://34.205.89.18/live/"+menuItemsInfo.get(index).get("p_id")+"to"+ menuItemsInfo.get(index).get("streamId"));
                startActivity(intent);
            });

            parentLinear.addView(outlinearLayout);

        }


    }


}