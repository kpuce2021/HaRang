package com.example.harangS.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.harangS.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment1 extends Fragment {
    public static Fragment1 newInstance() {
        return new Fragment1();
    }
    private View view;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;

    //private static String[] menuItems;
    public static ArrayList<HashMap<String,String>> menuItemsInfo; //영상들의 정보를 담는 리스트
    private static HashMap<String,String> menuItem;

    private static String TAG = "db_test";
    private static String IP = "44.196.58.43"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    private static int listCount = 0;
    private static VideoListViewAdapter adapter; //리스트 어답터
    private static String StudentId;
    private static String s_id;


    private static ArrayList<VideoListViewItem> items;
    @SuppressLint("StaticFieldLeak")
    private static ListView listview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);

        // Adapter 생성
        items = new ArrayList<VideoListViewItem>() ;
        
        //items 로드
        StudentId = BaseActivity.StudentId;
        s_id = BaseActivity.s_id;
        accessDB(StudentId);

        Button eyetracking = view.findViewById(R.id.eyetracking);
        eyetracking.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(mContext, EyetrackingActivity.class);
                intent.putExtra("user_id",BaseActivity.StudentId);
                intent.putExtra("s_id",BaseActivity.s_id);
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

    private void accessDB(final String studentId){
        //item 값 불러오기

        //start RDS
        String url = "http://" + IP + "/readVideoList.php";
        ContentValues addRowValue = new ContentValues();
        Log.i(TAG,"BaseActivity.StudentId : "+BaseActivity.StudentId);
        addRowValue.put("id",BaseActivity.StudentId);
        //addRowValue.put("id",MyApplication.user_id);

        selectDatabase selectDatabase = new selectDatabase(url, addRowValue);
        selectDatabase.execute(); // AsyncTask는 .excute()로 실행된다
        //end RDS
    }

    class selectDatabase extends AsyncTask<Void, Void, String> {

        private String url1;
        private ContentValues values1;
        String result1 = ""; // 요청 결과를 저장할 변수.

        public selectDatabase(String url, ContentValues contentValues) {
            this.url1 = url;
            this.values1 = contentValues;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result1 = requestHttpURLConnection.request(url1, values1); // 해당 URL로 부터 결과물을 얻어온다
            return result1; // 여기서 당장 실행 X, onPostExcute에서 실행

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null) {
                doJSONParser(s); // 파서로 전체 출력
            }
        }
    }


    // 받아온 json 데이터를 파싱합니다.
    public void doJSONParser(String string) {
        try {
            String result = "";
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data_video");

            listCount = jsonArray.length();
            menuItemsInfo = new ArrayList<>();
//            menuItems = new String[listCount];

            VideoListViewItem item;
            if (items == null) {
                items = new ArrayList<VideoListViewItem>();
            }
            HashMap<String, String> concentList;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject output = jsonArray.getJSONObject(i);
                /*result += output.getString("v_name")+ " / "+ output.getString("v_textfield")+ " / "+ output.getString("v_calendar")+ "\n";*/
                menuItem = new HashMap<>();
                menuItem.put("v_id",output.getString("v_id"));
                menuItem.put("v_name",output.getString("v_name"));
                menuItem.put("v_time",output.getString("v_time")); //****
                menuItem.put("v_textfield",output.getString("v_textfield"));
                menuItem.put("v_calendar",output.getString("v_calendar"));


                menuItem.put("v_concent1_start",output.getString("v_concent1_start"));//****
                menuItem.put("v_concent1_stop",output.getString("v_concent1_stop"));//****
                menuItem.put("v_concent2_start",output.getString("v_concent2_start"));//****
                menuItem.put("v_concent2_stop",output.getString("v_concent2_stop"));//****

                menuItem.put("c_total",output.getString("c_total").equals("null")?"0":output.getString("c_total"));
                menuItem.put("c_separate",output.getString("c_separate").equals("null")?"0":output.getString("c_separate"));
                menuItemsInfo.add(menuItem);

                item = new VideoListViewItem();
                item.setVideoName(output.getString("v_name"));
                item.setVid(output.getString("v_id"));
                item.setTotalProgress(Integer.parseInt(menuItem.get("c_total"))); //전체 집중도
                item.setConcentProgress(Integer.parseInt(menuItem.get("c_separate"))); //구간 집중도


                concentList = new HashMap<>();
                concentList.put("start1",menuItem.get("v_concent1_start"));
                concentList.put("stop1",menuItem.get("v_concent1_stop"));
                concentList.put("start2",menuItem.get("v_concent2_start"));
                concentList.put("stop2",menuItem.get("v_concent2_stop"));
                Integer concentCount = 0;

                for(int k=1;k<=2;k++){
                    Log.i("db_test","v_concent"+k+"_start : "+menuItem.get("v_concent"+k+"_start"));
                    if(!menuItem.get("v_concent"+k+"_start").equals("-00:00:01")){

                        concentCount +=1;
                    }
                }
                item.setConcentRange(menuItem.get("v_concent1_start"),menuItem.get("v_concent1_stop"),
                        menuItem.get("v_concent2_start"),menuItem.get("v_concent2_stop"),String.valueOf(concentCount));
                concentList.put("clipCount",String.valueOf(concentCount));
                BaseActivity.totalConcentList.put(menuItem.get("v_id"),concentList);

                items.add(item);

                Log.i("db_test", "v_id : "+menuItem.get("v_id")+", v_name : "+menuItem.get("v_name")+", v_time : "+menuItem.get("v_time"));
                //Log.i("db_test", "start1 : "+menuItem.get("v_concent1_start")+", stop1 : "+menuItem.get("v_concent1_stop")+", start2 : "+menuItem.get("v_concent2_start")+", stop2 : "+menuItem.get("v_concent2_stop"));
                Log.i("db_test", "start1 : "+BaseActivity.totalConcentList.get(menuItem.get("v_id")).get("start1")+", stop1 : "+BaseActivity.totalConcentList.get(menuItem.get("v_id")).get("stop1")+
                        ", start2 : "+BaseActivity.totalConcentList.get(menuItem.get("v_id")).get("start2")+", stop2 : "+BaseActivity.totalConcentList.get(menuItem.get("v_id")).get("stop2")+
                        ", clipCount : "+BaseActivity.totalConcentList.get(menuItem.get("v_id")).get("clipCount"));

            }


            //adapter 생성
            adapter = new VideoListViewAdapter(mActivity, R.layout.item_video_listview, items);
            // 리스트뷰 참조 및 Adapter달기
            listview = (ListView) view.findViewById(R.id.video_list);
            listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}