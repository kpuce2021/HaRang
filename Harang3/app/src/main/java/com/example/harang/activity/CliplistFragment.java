package com.example.harang.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.harang.R;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class CliplistFragment extends Fragment {
    public static CliplistFragment newInstance() {
        return new CliplistFragment();
    }
    private static Context mContext;
    private static Activity mActivity;

    public static ArrayList<HashMap<String,String>> menuItemsInfo; //영상들의 정보를 담는 리스트
    private static HashMap<String,String> menuItem;

    private static Bundle bundle;

    private static String VideoName;
    private static String studentId;
    private static String s_id;
    private static String v_id;

    private static View view;
    private static ClipvideoListAdapter adapter; //리스트 어답터
    private static ArrayList<ClipvideoListItem> items;
    private static ListView listview;


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
        if(videoInFolder()){
            //msecList 가져오기
            if(BaseActivity.totalList.containsKey(v_id)){
                //해당 비디오의 mSecList가 있음
                Log.i("cliptest","통과");
                initUI();
            }else{ //msecList가 없을경우 -> BaseActivity로 이동

                Log.i("cliptest","1 되돌아가기");
                ((BaseActivity)getContext()).replaceFragment(StudentFragment1.newInstance(),bundle);
            }

        }else{//해당 비디오가 없을 경우
            Log.i("cliptest","2 되돌아가기");
            ((BaseActivity)getContext()).replaceFragment(StudentFragment1.newInstance(),bundle);
        }

        return view;
    }

    /*
    1. 이전 프래그먼트에서 v_id, videoName 받아오기
    2. 해당 비디오가 사용자 핸드폰에 저장되어있는지 확인
    3. 있을경우 클립선택 페이지 정상적으로 열기
       없을경우 전체영상먼저 시청하라는 메시지 후 다시 BaseActivity로 이동
    4. mSecList 가져오기
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
        items = new ArrayList<ClipvideoListItem>() ;
        ClipvideoListItem item;

        int clipCount = 0;
        if(BaseActivity.totalList.get(v_id).size() <2){
            clipCount = BaseActivity.totalList.get(v_id).size();
        }else{
            clipCount=2;
        }

        for(int i=0;i<clipCount;i++){
            item = new ClipvideoListItem();
            item.setVideoName(VideoName+" - "+(i+1));
            item.setStartTime(BaseActivity.totalList.get(v_id).get(i).get("start"));
            item.setVideoLength(BaseActivity.totalList.get(v_id).get(i).get("length"));
            item.setEndTime(BaseActivity.totalList.get(v_id).get(i).get("start") + BaseActivity.totalList.get(v_id).get(i).get("length") - 1);
            items.add(item);
            Log.i("cliptest","이름 : "+VideoName+" - "+i + ", 시작 : "+BaseActivity.totalList.get(v_id).get(i).get("start") + ", 길이 : "+BaseActivity.totalList.get(v_id).get(i).get("length"));
        }

        adapter = new ClipvideoListAdapter(mActivity,R.layout.clipvideo_listview_item,items);
        listview = (ListView) view.findViewById(R.id.clip_video_list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //영상 자르기
                try {
                    convertAudio(VideoName, v_id, position);

                } catch (FFmpegCommandAlreadyRunningException | IOException e) {
                    Log.i("cliptest","ffmpeg 실패 : "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void convertAudio(String VideoName, String v_id, int position) throws FFmpegCommandAlreadyRunningException, IOException {
        FfmpegUtil.requestPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        FfmpegUtil.requestPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        AndroidAudioConverter.load(mContext, VideoName ,v_id, position);
    }
}
