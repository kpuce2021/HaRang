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

public class StudentFragment5  extends Fragment {
    public static StudentFragment4 newInstance() {
        return new StudentFragment4();
    }
    private View view;

    private static Context mContext;
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
        view = inflater.inflate(R.layout.stu_fragment3, container, false);

        //items 로드
        StudentId = BaseActivity.StudentId;
        s_id = BaseActivity.s_id;

        accessDB();


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
    private void accessDB(){
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    menuItemsInfo = new ArrayList<>();

                    listCount = Integer.parseInt(jsonObject.getString("count1"));
                    listCount += Integer.parseInt(jsonObject.getString("count2"));
                    Log.i("db_test","listCount : "+listCount);
                    for(int i=0;i<listCount;i++){
                        menuItem = new HashMap<>();
                        JSONObject output = jsonObject.getJSONObject(String.valueOf(i));

                        Log.i("db_test", " type : " + output.getString("type"));
                        Log.i("db_test", " v_id : " + output.getString("v_id"));
                        Log.i("db_test", " v_name : " + output.getString("v_name"));
                        Log.i("db_test", " p_name : " + output.getString("p_name"));
                        Log.i("db_test", " startTime : " + output.getString("startTime"));
                        Log.i("db_test", " p_id : " + output.getString("p_id"));
                        Log.i("db_test", " ");


                        menuItem.put("type",output.getString("type"));
                        menuItem.put("v_id",output.getString("v_id"));
                        menuItem.put("v_name",output.getString("v_name"));
                        menuItem.put("p_name",output.getString("p_name"));
                        menuItem.put("startTime",output.getString("startTime"));
                        menuItem.put("p_id",output.getString("p_id"));

                        menuItemsInfo.add(menuItem);
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

        StudentAllTypeVideoListRequset request = new StudentAllTypeVideoListRequset(s_id, responseListener);
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void initUI() {

        LinearLayout parentLinear = view.findViewById(R.id.allTypeVideList);
        parentLinear.removeAllViews();

        TextView[] tv = new TextView[5];
        for(int i=0;i<listCount;i++) {
            //사진,설명 담아놓은 가장 바깥 뷰  //weight 설정 없음
            LinearLayout outlinearLayout = new LinearLayout(mContext);
            outlinearLayout.setBackgroundResource(R.drawable.border_layout);
            outlinearLayout.setWeightSum(3);

//            ImageView imageview = new ImageView(mContext);
//            imageview.setImageResource(R.drawable.img1);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    200,
//                    300
//            );
//
//            params.setMargins(40,30,50,30);
//            imageview.setLayoutParams(params);
//            outlinearLayout.addView(imageview);


            //텍스트 영역
            LinearLayout inlinearLayout = new LinearLayout(mContext);//weight 설정 없음

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


            tv[0].setText(menuItemsInfo.get(i).get("p_name")+ " 선생님");
            inlinearLayout.addView(tv[0]);
            tv[1].setText("강의명 : " + menuItemsInfo.get(i).get("v_name"));
            inlinearLayout.addView(tv[1]);
            tv[2].setText("시작 시간 : "+menuItemsInfo.get(i).get("startTime"));
            inlinearLayout.addView(tv[2]);
            tv[3].setText("강의 타입 : "+(menuItemsInfo.get(i).get("type").equals("normal")?"동영상 강의":menuItemsInfo.get(i).get("type").equals("stream")?"실시간 강의":"실시간 파일 강의"));
            inlinearLayout.addView(tv[3]);

            outlinearLayout.addView(inlinearLayout);
            final int index = i;
            outlinearLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, StudentConcentRateActivity.class);
                intent.putExtra("type",menuItemsInfo.get(index).get("type"));
                intent.putExtra("v_id",menuItemsInfo.get(index).get("v_id"));
                intent.putExtra("v_name",menuItemsInfo.get(index).get("v_name"));
                intent.putExtra("p_id",menuItemsInfo.get(index).get("p_id"));
                startActivity(intent);
            });

            parentLinear.addView(outlinearLayout);

        }


    }
}
