package com.example.harangS.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harangS.R;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SClipListFragment extends Fragment {
    public static SClipListFragment newInstance() {
        return new SClipListFragment();
    }
    private static Context mContext;
    private static Activity mActivity;
    private ProgressDialog customProgressDialog;

    public static ArrayList<HashMap<String,String>> menuItemsInfo; //영상들의 정보를 담는 리스트
    private static HashMap<String,String> menuItem;

    private static Bundle bundle;

    private static String VideoName;
    private static String studentId;
    private static String s_id;
    private static String v_id;

    private static View view;
    private static SClipVideoListAdapter adapter; //리스트 어답터
    private static ArrayList<SClipVideoListItem> items;
    private static ListView listview;
    private static List<Integer> sortKeyList;


    private static ArrayList<HashMap<String,String>> clipVideoMap;
    private static HashMap<String,String> clipVideoItems;
    private int clipVideoCount;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_fragment_cliplist, container, false);
        
        Log.i("cliptest","clipfragment 진입");

        bundle = getArguments();  //번들 받기. getArguments() 메소드로 받음.
        VideoName = bundle.getString("videoName");
        studentId = bundle.getString("studentId");
        s_id = bundle.getString("s_id");
        v_id = bundle.getString("v_id");

        Bundle bundle = new Bundle();
        bundle.putString("user_id",studentId);
        bundle.putString("s_id",s_id);

        accessDB();


        return view;
    }

    private void accessDB(){
        //table 값 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {

                        clipVideoMap = new ArrayList<HashMap<String, String>>();

                        clipVideoCount = Integer.parseInt(jsonObject.getString("count"));
                        Log.i("db_test","videoCount : "+clipVideoCount);
                        for(int i=0;i<clipVideoCount;i++){
                            clipVideoItems = new HashMap<>();
                            JSONObject output = jsonObject.getJSONObject(String.valueOf(i));
                            /*
                            Log.i("db_test", " cv_id : " + output.getString("cv_id"));
                            Log.i("db_test", " cv_time : " + output.getString("cv_time"));
                            Log.i("db_test", " cv_stime : " + output.getString("cv_stime"));
                            Log.i("db_test", " cv_etime : " + output.getString("cv_etime"));
                            Log.i("db_test", " ");
                             */

                            clipVideoItems.put("cv_id",output.getString("cv_id"));
                            clipVideoItems.put("cv_time",output.getString("cv_time"));
                            clipVideoItems.put("cv_stime",output.getString("cv_stime"));
                            clipVideoItems.put("cv_etime",output.getString("cv_etime"));

                            clipVideoMap.add(clipVideoItems);


                        }
                        /*

                        //정렬
                        HashMap<Integer,String> listSet = new HashMap<>();
                        for(int i=0;i<clipVideoCount;i++){
                            listSet.put(i,clipVideoMap.get(i).get("v_calendar"));
                        }
                        sortKeyList = new ArrayList<>(listSet.keySet());
                        Collections.sort(sortKeyList, (o1, o2) -> (listSet.get(o1).compareTo(listSet.get(o2))));

                         */




                        if(clipVideoCount > 0){//해당 비디오의 클립영상 목록이 있을 때
                            Log.i("cliptest","통과");
                            initUI();
                        }else{//해당 비디오의 클립영상 목록이 없을때
                            Log.i("cliptest","1 되돌아가기");
                            ((BaseActivity)getContext()).replaceFragment(StudentFragment1.newInstance(),bundle);
                        }


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
        SClipListReadRequest sClipListReadRequest = new SClipListReadRequest(s_id, v_id, responseListener);
        sClipListReadRequest.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(sClipListReadRequest);


        //로딩용 딜레이 필요함


    }

    /*
    1. 이전 프래그먼트에서 v_id, videoName 받아오기
    2. 해당 비디오의 클립 정보가 db에 저장되어있는지 확인
    3. 있을경우 클립선택 페이지 정상적으로 열기
       없을경우 전체영상먼저 시청하라는 메시지 후 다시 BaseActivity로 이동
    4. db 정보 출력하기
     */
    

    private boolean videoInFolder(){
        String path = mContext.getExternalFilesDir(null).toString()  + "/" ;
        File directory = new File(path);
        File[] files = directory.listFiles();

        boolean temp = false;

        for(int i=0;i<files.length;i++){
            if(files[i].getName().equals(VideoName+".mp4")){
                temp = true;
            }
        }

        return temp;

    }

    private void initUI(){
        // Adapter 생성
        items = new ArrayList<SClipVideoListItem>() ;
        SClipVideoListItem item;


        for(int i=0;i<clipVideoCount;i++){
            item = new SClipVideoListItem();
            item.setVideoName(VideoName+" - "+(i+1));
            //db에서 받아온 정보 출력
            item.setStartTime(clipVideoMap.get(i).get("cv_stime"));
            item.setEndTime(clipVideoMap.get(i).get("cv_etime"));
            items.add(item);
            Log.i("cliptest","이름 : "+VideoName+" - "+i + ", 시작 : "+clipVideoMap.get(i).get("cv_stime") + ", 종료 : "+clipVideoMap.get(i).get("cv_etime"));
        }

        adapter = new SClipVideoListAdapter(mActivity,R.layout.clipvideo_listview_item,items);
        listview = (ListView) view.findViewById(R.id.clip_video_list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //
                /*
                1. 내장메모리에 전체 영상이 다운되어있는지 확인
                1-1. 있을 경우 계속 진행
                1-2. 없을 경우 다운로드 먼저 진행
                2. 클립영상 생성
                */
                customProgressDialog = new ProgressDialog(getContext());
                customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                customProgressDialog.show();


                //1. 내장메모리에 전체 영상이 다운되어있는지 확인
                if(!videoInFolder()){
                    //1-2. 영상 다운로드

                }

                //2. 클립 영상 생성
                try {

                    convertAudio(VideoName, v_id, clipVideoMap, position);

                } catch (FFmpegCommandAlreadyRunningException | IOException e) {
                    Log.i("cliptest","ffmpeg 실패 : "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void convertAudio(String VideoName, String v_id, ArrayList<HashMap<String,String>> clipVideoMap, int position) throws FFmpegCommandAlreadyRunningException, IOException {
        FfmpegUtil.requestPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        FfmpegUtil.requestPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        SClipAudioConverter.load(mContext, VideoName ,v_id, clipVideoMap, position);

    }
}
