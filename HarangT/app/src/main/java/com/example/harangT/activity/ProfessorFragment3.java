package com.example.harangT.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangT.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment3 extends Fragment {
    public static ProfessorFragment3 newInstance() {
        return new ProfessorFragment3();
    }
    public ProfessorFragment3(){ }


    private View view;

    private static Context mContext;
    private static Activity mActivity;

    private static String ProfessorId;
    private static String p_id;
    private static ArrayList<HashMap<String,String>> menuItemsInfo;
    private static HashMap<String,String> menuItem;
    private static List<Integer> sortKeyList;
    private int videoCount;

    private static int listCount = 0;
    private static String[] items = {"최신순", "이름순"/*, "집중도순"*/};
    private static ListView listview;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment3, container, false);

        ProfessorId = PBaseActivity.ProfessorId;
        p_id = PBaseActivity.p_id;

        //데이터로딩
        accessDB(p_id);


        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //기본 : 최신순
                HashMap<Integer,String> listSet = new HashMap<>();

                for(int i=0;i<videoCount;i++){
                    if(position == 0){//최신순
                        listSet.put(i, menuItemsInfo.get(i).get("startTime"));
                    }else if(position == 1){//이름순
                        listSet.put(i, menuItemsInfo.get(i).get("v_name"));
                    }/*else if(position==2){ //집중순
                        listSet.put(i, menuItemsInfo.get(i).get("v_concent"));
                    }*/
                }
                sortKeyList = new ArrayList<>(listSet.keySet());
                Collections.sort(sortKeyList, (o1, o2) -> (listSet.get(o1).compareTo(listSet.get(o2))));

                initUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accessDB(final String p_id){
        //table 값 불러오기
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    menuItemsInfo = new ArrayList<>();

                    listCount = Integer.parseInt(jsonObject.getString("count1"));
                    listCount += Integer.parseInt(jsonObject.getString("count2"));
                    Log.i("db_test","listCount : "+listCount+", p_id : "+p_id);
                    for(int i=0;i<listCount;i++){
                        menuItem = new HashMap<>();
                        JSONObject output = jsonObject.getJSONObject(String.valueOf(i));

                        Log.i("db_test", " type : " + output.getString("type"));
                        Log.i("db_test", " v_id : " + output.getString("v_id"));
                        Log.i("db_test", " v_name : " + output.getString("v_name"));
                        Log.i("db_test", " startTime : " + output.getString("startTime"));
                        Log.i("db_test", " ");


                        menuItem.put("type",output.getString("type"));
                        menuItem.put("v_id",output.getString("v_id"));
                        menuItem.put("v_name",output.getString("v_name"));
                        menuItem.put("startTime",output.getString("startTime"));

                        menuItemsInfo.add(menuItem);
                    }

                    HashMap<Integer,String> listSet = new HashMap<>();
                    for(int i=0;i<videoCount;i++){
                        listSet.put(i, menuItemsInfo.get(i).get("startTime"));
                    }
                    sortKeyList = new ArrayList<>(listSet.keySet());
                    Collections.sort(sortKeyList, (o1, o2) -> (listSet.get(o1).compareTo(listSet.get(o2))));

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
        ProfessorAllTypeVideoListRequset pVideoListReadRequest = new ProfessorAllTypeVideoListRequset(p_id, responseListener);
        pVideoListReadRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(pVideoListReadRequest);


        //로딩용 딜레이 필요함


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private void initUI(){

        LinearLayout parentLinear = view.findViewById(R.id.allTypeVideList);
        parentLinear.removeAllViews();

        TextView[] tv = new TextView[5];
        for(int i=0;i<listCount;i++) {
            //사진,설명 담아놓은 가장 바깥 뷰  //weight 설정 없음
            LinearLayout outlinearLayout = new LinearLayout(mContext);
            outlinearLayout.setBackgroundResource(R.drawable.border_layout);
            outlinearLayout.setWeightSum(3);

            //썸네일
//            ImageView imageview = new ImageView(mContext);
//            imageview.setImageResource(R.drawable.img1);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
            for(int j=0;j<5;j++){
                tv[j] = new TextView(mContext);
                tv[j].setTextColor(Color.BLACK);

                tv[j].setTextSize(20);
                /*Typeface typeface = getResources().getFont(R.font.font);
                tv[j].setTypeface(typeface);*/
            }


            tv[1].setText("     강의명 : " + menuItemsInfo.get(i).get("v_name"));
            inlinearLayout.addView(tv[1]);
            tv[2].setText("     시작 시간 : "+menuItemsInfo.get(i).get("startTime"));
            inlinearLayout.addView(tv[2]);
            tv[3].setText("     강의 타입 : "+(menuItemsInfo.get(i).get("type").equals("normal")?"동영상 강의":menuItemsInfo.get(i).get("type").equals("stream")?"실시간 강의":"실시간 파일 강의"));
            inlinearLayout.addView(tv[3]);
            tv[4].setText("");
            inlinearLayout.addView(tv[4]);


            outlinearLayout.addView(inlinearLayout);
            final int index = i;
            outlinearLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PConcentStudentListActivity.class);
                intent.putExtra("type",menuItemsInfo.get(index).get("type"));
                intent.putExtra("v_id",menuItemsInfo.get(index).get("v_id"));
                intent.putExtra("v_name",menuItemsInfo.get(index).get("v_name"));
                startActivity(intent);
            });

            parentLinear.addView(outlinearLayout);
        }


    }





}

/*
1. 영상목록 출력 O
2. 영상 정보 수정 기능
3. 영상 정렬

 */