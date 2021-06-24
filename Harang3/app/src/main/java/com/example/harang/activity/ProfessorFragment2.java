package com.example.harang.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harang.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfessorFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessorFragment2 extends Fragment {
    public static ProfessorFragment2 newInstance() {
        return new ProfessorFragment2();
    }
    public ProfessorFragment2(){ }


    private View view;

    private static Context mContext;
    private static Activity mActivity;

    private static String ProfessorId;
    private static String p_id;
    private static ArrayList<HashMap<String,String>> videoMap;
    private static HashMap<String,String> videoItems;
    private static List<Integer> sortKeyList;
    private int videoCount;

    private static String[] items = {"최신순", "이름순", "집중도순"};
    private static ListView listview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p_fragment2, container, false);

        ProfessorId = p_BaseActivity.ProfessorId;
        p_id = p_BaseActivity.p_id;

        //데이터로딩
        accessDB(p_id);


        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //기본 : 최신순
                HashMap<Integer,String> listSet = new HashMap<>();

                for(int i=0;i<videoCount;i++){
                    if(position == 0){//최신순
                        listSet.put(i,videoMap.get(i).get("v_calendar"));
                    }else if(position == 1){//이름순
                        listSet.put(i,videoMap.get(i).get("v_name"));
                    }else if(position==2){ //집중순
                        listSet.put(i,videoMap.get(i).get("v_concent"));
                    }
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


    private void accessDB(final String p_id){
        //table 값 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {

                        videoMap = new ArrayList<HashMap<String, String>>();

                        videoCount = Integer.parseInt(jsonObject.getString("count"));
                        Log.i("db_test","videoCount : "+videoCount);
                        for(int i=0;i<videoCount;i++){
                            videoItems = new HashMap<>();
                            JSONObject output = jsonObject.getJSONObject(String.valueOf(i));
                            /*
                            Log.i("db_test", " v_id : " + output.getString("v_id"));
                            Log.i("db_test", " p_id : " + output.getString("p_id"));
                            Log.i("db_test", " v_name : " + output.getString("v_name"));
                            Log.i("db_test", " v_time : " + output.getString("v_time"));
                            Log.i("db_test", " v_textfield : " + output.getString("v_textfield"));
                            Log.i("db_test", " v_calendar : " + output.getString("v_calendar"));
                            Log.i("db_test", " c_concent1_start : " + output.getString("c_concent1_start"));
                            Log.i("db_test", " c_concent1_stop : " + output.getString("c_concent1_stop"));
                            Log.i("db_test", " c_concent2_start : " + output.getString("c_concent2_start"));
                            Log.i("db_test", " c_concent2_stop : " + output.getString("c_concent2_stop"));
                            Log.i("db_test", " v_attend : " + output.getString("v_attend"));
                            Log.i("db_test", " v_concent : " + output.getString("v_concent"));
                            Log.i("db_test", " ");
                             */

                            videoItems.put("v_id",output.getString("v_id"));
                            videoItems.put("p_id",output.getString("p_id"));
                            videoItems.put("v_name",output.getString("v_name"));
                            videoItems.put("v_time",output.getString("v_time"));
                            videoItems.put("v_textfield",output.getString("v_textfield"));
                            videoItems.put("v_calendar",output.getString("v_calendar"));
                            videoItems.put("c_concent1_start",output.getString("c_concent1_start"));
                            videoItems.put("c_concent1_stop",output.getString("c_concent1_stop"));
                            videoItems.put("c_concent2_start",output.getString("c_concent2_start"));
                            videoItems.put("c_concent2_stop",output.getString("c_concent2_stop"));
                            videoItems.put("v_attend",output.getString("v_attend"));
                            videoItems.put("v_concent",output.getString("v_concent"));

                            videoMap.add(videoItems);


                            /*
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.attach(ProfessorFragment3.this);
                            ft.commit();*/
                        }

                        HashMap<Integer,String> listSet = new HashMap<>();
                        for(int i=0;i<videoCount;i++){
                            listSet.put(i,videoMap.get(i).get("v_calendar"));
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

            }
        };
        // 서버로 Volley를 이용해서 요청을 함.
        PVideoListReadRequest pVideoListReadRequest = new PVideoListReadRequest(p_id, responseListener);
        pVideoListReadRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(pVideoListReadRequest);


        //로딩용 딜레이 필요함


    }


    @SuppressLint("ResourceAsColor")
    private void initUI(){

        ScrollView parentScrollView = view.findViewById(R.id.tableLayout);
        LinearLayout parentLinear = view.findViewById(R.id.scrollLinear);
        parentLinear.removeAllViews();

        TextView[] tv = new TextView[5];
        for(int i=0;i<videoCount;i++) {
            //사진,설명 담아놓은 가장 바깥 뷰  //weight 설정 없음
            LinearLayout outlinearLayout = new LinearLayout(mContext);
            outlinearLayout.setBackgroundResource(R.drawable.border_layout);
            outlinearLayout.setWeightSum(3);

            //썸네일
            ImageView imageview = new ImageView(mContext);
            imageview.setImageResource(R.drawable.img1);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40,30,50,30);
            imageview.setLayoutParams(params);
            outlinearLayout.addView(imageview);


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
                tv[j].setTextSize(15);
                tv[j].setTextColor(Color.BLACK);
            }


            tv[0].setText("제목 : " + videoMap.get(sortKeyList.get(i)).get("v_name"));
            inlinearLayout.addView(tv[0]);
            tv[1].setText("업로드 날짜 : " + videoMap.get(sortKeyList.get(i)).get("v_calendar"));
            inlinearLayout.addView(tv[1]);
            tv[2].setText(videoMap.get(i).get("v_textfield"));
            inlinearLayout.addView(tv[2]);
            tv[3].setText("전체 출석률 : " + videoMap.get(sortKeyList.get(i)).get("v_attend") + "%");
            inlinearLayout.addView(tv[3]);
            tv[4].setText("전체 학생 집중도 : " + videoMap.get(sortKeyList.get(i)).get("v_concent") + "%");
            inlinearLayout.addView(tv[4]);

            outlinearLayout.addView(inlinearLayout);


            parentLinear.addView(outlinearLayout);
        }


    }





}

/*
1. 영상목록 출력 O
2. 영상 정보 수정 기능
3. 영상 정렬

 */