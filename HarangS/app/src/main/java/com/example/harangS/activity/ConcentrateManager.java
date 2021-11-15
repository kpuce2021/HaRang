package com.example.harangS.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.harangS.R;

import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.state.ScreenState;

import static camp.visual.gazetracker.state.ScreenState.OUTSIDE_OF_SCREEN;
import static com.example.harangS.activity.BaseActivity.s_name;

public class ConcentrateManager{
    static private ConcentrateManager mInstance = null; //인스턴스
    private static String TAG = "printTest";
    private static Context mContext;
    Dialog dialog;
    Button btn_popup;
    TextView textView;
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
    static int popupCount = 0; //화면 외부를 볼경우 카운트 증가
    static private int estate= 0; //eyemovement(FIXATION, SACCADE)
    static private int mstate = 0; //screenstate(INSIDE, OUTSIDE)
    private static String StudentId;
    private static String v_id;

    private static String start1;
    private static String start2;
    private static String stop1;
    private static String stop2;
    private static String SclipCount;

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

        if(gazeInfo.screenState == null){
            mstate = 0;
            popupCount++;
        }else if(gazeInfo.screenState == ScreenState.INSIDE_OF_SCREEN){
            mstate = 1;
            popupCount -= popupCount<=0?0:1;
        }else{
            mstate = 0;
            popupCount++;
        }


        if(popupCount >= 150){
            show();
            popupCount = 0;
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
    }

    public void setConcentrate(){
        //1. 전체 집중률
        c_total = getConcentrateRate(0,eyeSecondCount);

        //2. 구간 집중률
        int clipCount = Integer.parseInt(SclipCount);
        int[] concent_start = new int[2];
        int[] concent_end = new int[2];
        c_seperate = 0;
        if(clipCount == 0){
            concent_start[0] = 0;
            concent_end[0] = eyeSecondCount;
            clipCount=1;
        }else{
            concent_start[0] = Integer.parseInt(start1.substring(0,2))*3600 + Integer.parseInt(start1.substring(3,5))*60 + Integer.parseInt(start1.substring(6,8));
            concent_end[0] = Integer.parseInt(stop1.substring(0,2))*3600 + Integer.parseInt(stop1.substring(3,5))*60 + Integer.parseInt(stop1.substring(6,8));
            if(clipCount==2){
                concent_start[1] = Integer.parseInt(start2.substring(0,2))*3600 + Integer.parseInt(start2.substring(3,5))*60 + Integer.parseInt(start1.substring(6,8));
                concent_end[1] = Integer.parseInt(stop2.substring(0,2))*3600 + Integer.parseInt(stop2.substring(3,5))*60 + Integer.parseInt(stop1.substring(6,8));
            }
        }

        for(int i=0;i<clipCount;i++){
            c_seperate += getConcentrateRate(concent_start[i],concent_end[i]);
        }
        Log.i(TAG,"전체집중률 : "+c_total+", 구간집중률 : "+c_seperate);
    }

    public double getConcentrateRate(int startTime, int endTime){
        double totalConcent = 0;
        for(int i=startTime;(i<=endTime && i<eyeSecondCount);i++){
            if(eyetrackData.get(i) == 1){
                totalConcent +=1;
            }
        }
        return  totalConcent/(endTime-startTime);
    }

    public void setClipvideoData(){
        //1. k초 설정
        int concentLine = 0; // k가 되는 값. 집중의 기준 시간
        if(eyeSecondCount<1800){
            concentLine = 5;
        }else{
            concentLine = (eyeSecondCount*10/3600);
        }

        //2. 집중시간이 적은 구간을 집중안함으로 변경하기위한 for문
        int maintainSec = 0;
        int clipCount = 1;

        int[] concent_start = new int[2];
        int[] concent_end = new int[2];

        if(clipCount == 0){
            concent_start[0] = 0;
            concent_end[0] = eyeSecondCount;
            clipCount=1;
        }else{
            concent_start[0] = Integer.parseInt(clipCount  start1.substring(0,2))*3600 + Integer.parseInt(start1.substring(3,5))*60 + Integer.parseInt(start1.substring(6,8));
            concent_end[0] = Integer.parseInt(stop1.substring(0,2))*3600 + Integer.parseInt(stop1.substring(3,5))*60 + Integer.parseInt(stop1.substring(6,8));
            if(clipCount==2){
                concent_start[1] = Integer.parseInt(start2.substring(0,2))*3600 + Integer.parseInt(start2.substring(3,5))*60 + Integer.parseInt(start1.substring(6,8));
                concent_end[1] = Integer.parseInt(stop2.substring(0,2))*3600 + Integer.parseInt(stop2.substring(3,5))*60 + Integer.parseInt(stop1.substring(6,8));
            }
        }

        //3. 비집중 부분을 리스트에 넣기 ("start" : 시작 초, "length" : 유지 시간)
        mSecList = new ArrayList<>(); //클립영상 리스트
        HashMap<String,Integer> addArrayList;
        for(int i=0;i<clipCount;i++){
            for(int j=concent_start[i];(j<=concent_end[i] && j<eyeSecondCount);j++){ //집중구간 한정으로 for문 돌기 시작
                if(eyetrackData.get(j) == 0 && (j== concent_end[i] || j == eyetrackData.size()-1)){ //이번이 마지막 초일때
                    maintainSec+=1;
                    addArrayList = new HashMap<>();
                    addArrayList.put("start",j-maintainSec+1);
                    addArrayList.put("length",maintainSec);
                    mSecList.add(addArrayList);
                    maintainSec = 0;
                }else if(eyetrackData.get(j) == 0 && (eyetrackData.get(j+1) == 0)){ //다음에도 비집중일때
                    maintainSec+=1;
                }else if(eyetrackData.get(j) == 0 && eyetrackData.get(j+1) == 1){ //다음 초때 집중
                    maintainSec+=1;
                    addArrayList = new HashMap<>();
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
        Collections.sort(mSecList, (o1, o2) -> {
            Integer v1 = o1.get("length");
            Integer v2 = o2.get("length");
            return v2.compareTo(v1);
        });
    }

    public static void getUserInfo(String tv_id, String ts_id,String dbstart1,String dbstart2,
                                String dbstop1, String dbstop2,String dbclipCount){
        v_id = tv_id;
        StudentId = ts_id;
        start1 = dbstart1;
        start2 = dbstart2;
        stop1 = dbstop1;
        stop2 = dbstop2;
        SclipCount = dbclipCount;

    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void accessDB(){
        
        setEyetrackData();
        setClipvideoData();
        setConcentrate();
        concentDB();
    }

    private void setEyetrackData() {
        //1. k초 설정
        int concentLine = 0; // k가 되는 값. 집중의 기준 시간
        if(eyeSecondCount<1800){
            concentLine = 5;
        }else{
            concentLine = (eyeSecondCount*10/3600);
        }


        int[] maintainSec = new int[3];
        for (int i = 0; i < eyeSecondCount; i++) { // 전체 영상 구간 반복문
            // 2-1. estate : '움직임'시간이 k초 미만인 부분은 ‘고정’으로 변경
            if ((i == eyeSecondCount - 1) && estateData.get(i) == 0) { //마지막 초때 상태가 '움직임'
                maintainSec[0] += 1;
                if (maintainSec[0] < concentLine) {
                    for (int j = i - maintainSec[0] + 1; j <= i; j++) {
                        estateData.put(j, 1);
                    }
                }
                maintainSec[0] = 0;
            }
            if (estateData.get(i) == 0) { //상태가 '움직임'
                maintainSec[0] += 1;
            } else{ //상태가 '고정'
                if (maintainSec[0] < concentLine) {
                    for (int j = i - maintainSec[0]; j < i; j++) {
                        estateData.put(j, 1);
                    }
                }
                maintainSec[0] = 0;
            }


            // 2-2. mstate : '응시 안함'시간이 3초 이하인 부분은 ‘응시’로 변경
            if ((i == eyeSecondCount - 1) && mstateData.get(i) == 0) {//마지막 초때 상태가 '응시안함'
                maintainSec[1] += 1;
                if (maintainSec[1] <= 3) {
                    for (int j = i - maintainSec[1] + 1; j <= i; j++) {
                        mstateData.put(j,1);
                    }
                }
                maintainSec[1] = 0;
            }

            if (mstateData.get(i) == 0) {//상태가 '응시 안함'
                maintainSec[1] += 1;
            } else {
                if (maintainSec[1] <= 3) { //상태가 '응시'
                    for (int j = i - maintainSec[1]; j < i; j++) {
                        mstateData.put(j,1);
                    }
                }
                maintainSec[1] = 0;
            }


            //2-3.두 정보의 AND 연산 결과를 eyetrackData에 저장
            if (!eyetrackData.containsKey(i)) { // 값을 처음 넣을 때
                eyetrackData.put(i, estateData.get(i) & mstateData.get(i));
            } else { // 이미 값이 있을 때때
                if (eyetrackData.get(i) == 1 && (estateData.get(i) & mstateData.get(i)) == 1) {
                    eyetrackData.put(i, 1);
                } else {
                    eyetrackData.put(i, 0);
                }
            }
        }


    }

    public void show(){
        if(dialog != null){
            dialog.dismiss();
        }
            dialog = new Dialog(ConcentrateManager.mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);


            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            textView = dialog.findViewById(R.id.tv_dialog);
            btn_popup = dialog.findViewById(R.id.btn_popup);
            textView.setText(s_name+" 학생 수업에 집중해주세요");
            btn_popup.setOnClickListener(view -> {
                popupCount = 0;
                dialog.dismiss();
            });
        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void concentDB(){
        ConcentDataStorage cds = new ConcentDataStorage();
        cds.initConcentDataStorage(v_id,StudentId,eyetrackData,estateData,mstateData,eyeSecondCount,mSecList,c_total,c_seperate,mContext);
    }
}
