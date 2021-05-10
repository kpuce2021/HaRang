package com.example.harang.activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.harang.GazeTrackerManager;
import com.example.harang.view.PointView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.state.ScreenState;
/*
 * 액티비티에서 아이트래킹 정보 받아오기 0
 * 액티비티에서 영상 정보 받아오기
 * 시선데이터 테이블 값 계산 하기 0
 * 시선데이터 테이블 값 db저장
 * 학생 집중정보 테이블 값 계산하기 0
 * 학생 집중정보 테이블 값 db 저장
 * 클립영상 데이터 테이블 값 계산하기
 * 클립영상 데이터 테이블 값 db저장
 *
 * */

public class ConcentrateManager{
    static private ConcentrateManager mInstance = null; //인스턴스
    private static String TAG = "printTest";
    private static Context mContext;
    //객체를 다른 액티비티에서 이어서 쓸 수 있지만 생성자로 모두 초기화해서 몇초 동안의 값이 저장되었는지에 대한 정보가 필요함
    /*
    estateData : 초 단위로 overwrite되어 저장되는 eyeMovementState (fixation - 1, saccade - 0) 정보
    mstateData : 초 단위로 overwrite되어 저장되는 screenState (INSIDE_OF_SCREEN - 1, OUTSIDE_OF_SCREEN - 0) 정보
    eyetrackData : 초 단위로 들어온 위 2가지 정보의 and 연산값 저장
    */

    static private HashMap<Integer,Integer> eyetrackData = new HashMap<>();
    static private HashMap<Integer,Integer> estateData = new HashMap<>();
    static private HashMap<Integer,Integer> mstateData = new HashMap<>();
    static int eyeSecondCount = 0; //저장된 시간이 몇초인지 카운트 하기위한 변수
    static int initTimestamp = -1; //초반 타임스탬프 값이 랜덤하게 들어오기 때문에 기준값을 저장하기 위한 변수
    static private int estate= 0; //eyemovement(FIXATION, SACCADE)
    static private int mstate = 0; //screenstate(INSIDE, OUTSIDE)
    private static String StudentId;
    private static String v_id;

    private static ArrayList<HashMap<String,Integer>> mSecList; //클립영상 리스트
    private static double c_total;
    private static double c_seperate;

    //생성자로 액티비티 불러오기
    public ConcentrateManager(Context context) {
        mContext = context;
    }

    public void getContext(Context context){
        mContext = context;
    }

    static public ConcentrateManager makeNewInstance(Context context){ //인스턴스를 처음 사용할 때
        mInstance = new ConcentrateManager(context);
        eyetrackData.clear();
        estateData.clear();
        mstateData.clear();
        eyeSecondCount = 0;
        initTimestamp = -1;
        estate= 0;
        mstate = 0;
        /*
        if (mInstance != null) {}
        else{
            mInstance = new ConcentrateManager(context);
        }
        */
        return mInstance;
    }

    static public ConcentrateManager getInstance() {
        return mInstance;
    } //다른 액티비티에서 인스턴스를 사용할 때


    //아이트래킹 액티비티에서 아이트래킹 정보를 받아오는 함수
    //추후 매개변수로 영상 시간값도 필요(아이트래킹 시간 & 영상 시간 일치 확인 후 저장이 필요)
    //프레임단위로 실행됨
    public void getEyetrackingData(GazeInfo gazeInfo, Integer timestamp){
        //Log.i("getEyetrackingData","eyeMove : "+gazeInfo.eyeMovementState + " screen : "+gazeInfo.screenState);

        if(initTimestamp == -1){//초반 타임스탬프 값이 랜덤하게 들어오기 때문에 기준값을 저장하기 위한 변수
            initTimestamp = timestamp;
            //초반에 한번만 저장하기 때문에 후에 영상이 일시정지 하는 상황이 있을 경우 기준값 변경이 필요
        }
        eyeSecondCount = timestamp - initTimestamp; //일단 전체 영상 시간을 전체 실행시간으로 확인

        if(gazeInfo.eyeMovementState == null){ estate = 0; }
        else if(gazeInfo.eyeMovementState == EyeMovementState.FIXATION){
            estate = 1;
        }else{
            estate = 0;
        }

        if(gazeInfo.screenState == null){ mstate = 0; }
        else if(gazeInfo.screenState == ScreenState.INSIDE_OF_SCREEN){
            mstate = 1;
        }else{
            mstate = 0;
        }


        int countSec = timestamp - initTimestamp;
        if(!estateData.containsKey(countSec)){ //값을 처음 넣을 때
            estateData.put(countSec,estate);
        }else{ //이미 값이 있을 때때
            if(estateData.get(countSec) == 1 && estate == 1 ){
                estateData.put(countSec,1);
            }else{
                estateData.put(countSec,0);
            }
        }

        if(!mstateData.containsKey(countSec)){ //값을 처음 넣을 때
            mstateData.put(countSec,mstate);
        }else{ //이미 값이 있을 때때
            if(mstateData.get(countSec) == 1 && mstate == 1 ){
                mstateData.put(countSec,1);
            }else{
                mstateData.put(countSec,0);
            }
        }

        if(!eyetrackData.containsKey(countSec)){ //값을 처음 넣을 때
            eyetrackData.put(countSec,estate & mstate);
        }else{ //이미 값이 있을 때때
            if(eyetrackData.get(countSec) == 1 && (estate & mstate) == 1 ){
                eyetrackData.put(countSec,1);
            }else{
                eyetrackData.put(countSec,0);
            }
        }
    }

    //학생 집중정보 테이블 data_concentrate에 들어가는 정보인 전체집중률 c_total, 구간집중률 c_seperate 정보 계산 후 저장
    public void setConcentrate(){
        //1. 전체 집중률
        c_total = getConcentrateRate(0,eyeSecondCount); //db에 저장하는 작업 필요******

        //2. 구간 집중률
        int clipCount = 1; //클립구간 갯수에 대한 정보 필요********
        int[] concent_start = new int[clipCount];
        int[] concent_end = new int[clipCount];
        c_seperate = 0; //db에 저장하는 작업 필요******
        for(int i=0;i<clipCount;i++){
            concent_start[i] = 0; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
            concent_end[i] = 10; //db에서 받아오는 작업 필요**********
            c_seperate += getConcentrateRate(concent_start[i],concent_end[i]);
        }
        Log.i(TAG,"전체집중률 : "+c_total+", 구간집중률 : "+c_seperate);
    }

    //startTime ~ endTime 까지 집중 비율 계산
    public double getConcentrateRate(int startTime, int endTime){
        double totalConcent = 0;
        for(int i=startTime;(i<=endTime && i<eyeSecondCount);i++){
            if(eyetrackData.get(i) == 1){
                totalConcent +=1;
            }
        }
        return  totalConcent/(endTime-startTime);
    }

    //클립영상 구간 추출
    public void setClipvideoData(){
        //1. k초 설정
        int concentLine = 0; // k가 되는 값. 집중의 기준 시간
        //eyeSecondCount = eyetrackData.length;
        if(eyeSecondCount<1800){
            concentLine = 5;
        }else{
            concentLine = (eyeSecondCount*10/3600);
        }

        //2. 집중시간이 적은 구간을 집중안함으로 변경하기위한 for문
        int maintainSec = 0;
        for(int i=0;i<eyeSecondCount;i++){ //전체 영상 구간 반복문
            if(eyetrackData.get(i) == 1){
                maintainSec+=1;
            }else{
                if(maintainSec<=concentLine){
                    for(int j=i-maintainSec;j<i;j++){
                        eyetrackData.put(j, 0);
                    }
                }
                maintainSec = 0;
            }
        }

        maintainSec = 0;
        int clipCount = 2; //클립구간 갯수에 대한 정보 필요********
        int[] concent_start = new int[clipCount];
        int[] concent_end = new int[clipCount];

        /*
        //db에 저장되어있는 교수자가 입력한 영상에 대한 기록 받아오기
        for(int i=0;i<clipCount;i++){ //클립영상 구간 받아오기
            concent_start[i] = 0; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
            concent_end[i] = 10; //db에서 받아오는 작업 필요**********
        }
        */
        concent_start[0] = 0; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
        concent_end[0] = 8; //db에서 받아오는 작업 필요**********

        concent_start[1] = 13; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
        concent_end[1] = 20; //db에서 받아오는 작업 필요**********

        //3. 비집중 부분을 리스트에 넣기 ("start" : 시작 초, "length" : 유지 시간)
        mSecList = new ArrayList<>(); //클립영상 리스트
        HashMap<String,Integer> addArrayList;
        for(int i=0;i<clipCount;i++){
            for(int j=concent_start[i];(j<=concent_end[i] && j<eyeSecondCount);j++){ //집중구간 한정으로 for문 돌기 시작
                if(eyetrackData.get(j) == 0 && (j== concent_end[i] || j == eyeSecondCount)){ //이번이 마지막 초일때
                    maintainSec+=1;
                    addArrayList = new HashMap<String,Integer>();
                    addArrayList.put("start",j-maintainSec+1);
                    addArrayList.put("length",maintainSec);
                    mSecList.add(addArrayList);
                    maintainSec = 0;
                }else if(eyetrackData.get(j) == 0 && (eyetrackData.get(j+1) == 0)){ //다음에도 비집중일때
                    maintainSec+=1;
                }else if(eyetrackData.get(j) == 0 && eyetrackData.get(j+1) == 1){ //다음 초때 집중
                    maintainSec+=1;
                    addArrayList = new HashMap<String,Integer>();
                    addArrayList.put("start",j-maintainSec+1);
                    addArrayList.put("length",maintainSec);
                    mSecList.add(addArrayList);
                    maintainSec = 0;
                }else{//집중값이 1이 나왔을 경우 시간 초기화
                    maintainSec = 0;
                }
            }
            maintainSec = 0; //한 집중구간이 끝난 후 누적시간 초기화
        }

        // hashmap에서 value 값이 큰 순서로 정렬
        Collections.sort(mSecList, new Comparator<HashMap<String, Integer>>() {
            @Override
            public int compare(HashMap<String, Integer> o1, HashMap<String, Integer> o2) {
                Integer v1 = (Integer) o1.get("length");
                Integer v2 = (Integer) o2.get("length");
                return v2.compareTo(v1);
            }
        });

        //mSecList 정렬 성공
        Log.i(TAG,"클립영상 리스트");
        for(int i=0;i<mSecList.size();i++){
            Log.i(TAG,i+" - start : "+mSecList.get(i).get("start")+", length : "+mSecList.get(i).get("length"));
        }

    }

    public static void getUserInfo(String tv_id, String ts_id){
        //db에 집어넣기
        v_id = tv_id;
        StudentId = ts_id;
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void accessDB(){
        setClipvideoData();
        setConcentrate();

        Log.i(TAG,"수집 데이터 리스트");
        for(int i=0;i<eyeSecondCount;i++){
            Log.i(TAG, +eyetrackData.get(i)+" "+estateData.get(i)+" "+mstateData.get(i));
        }
        BaseActivity.totalList.put(v_id,mSecList);
        concentDB();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void concentDB(){
        ConcentDataStorage cds = new ConcentDataStorage();
        cds.initConcentDataStorage(v_id,StudentId,eyetrackData,estateData,mstateData,eyeSecondCount,mSecList,c_total,c_seperate,mContext);
    }

}