package com.example.harang.activity;


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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.harang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentFragment1 extends Fragment {
    public static StudentFragment1 newInstance() {
        return new StudentFragment1();
    }
    private View view;

    private static Context mContext;
    private static Activity mActivity;

    private static String[] menuItems;
    private static String TAG = "db_test";
    private static String IP = "3.214.234.24"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    private static int listCount = 0;
    private static VideoListViewAdapter adapter; //리스트 어답터

    private static ArrayList<VideoListViewItem> items;
    private static ListView listview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_fragment1, container, false);

        // Adapter 생성
        items = new ArrayList<VideoListViewItem>() ;

        //items 로드
        accessDB(BaseActivity.StudentId);

        Button eyetracking = view.findViewById(R.id.eyetracking);
        eyetracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EyetrackingActivity.class);
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
                menuItems = new String[listCount];

                VideoListViewItem item;
                if (items == null) {
                    items = new ArrayList<VideoListViewItem>();
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject output = jsonArray.getJSONObject(i);
                    /*result += output.getString("v_name")+ " / "+ output.getString("v_textfield")+ " / "+ output.getString("v_calendar")+ "\n";*/
                    menuItems[i] = output.getString("v_name");

                    item = new VideoListViewItem();
                    item.setVideoThumbnail(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_account_box_24));
                    item.setVideoName(output.getString("v_name"));
                    item.setTotalProgress(10);
                    item.setConcentProgress(20);
                    items.add(item);

                }

                //adapter 생성
                adapter = new VideoListViewAdapter(mActivity, R.layout.video_listview_item, items);
                // 리스트뷰 참조 및 Adapter달기
                listview = (ListView) view.findViewById(R.id.video_list);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}