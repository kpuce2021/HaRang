package com.example.harang.activity;

import android.content.Context;
import android.util.Log;

import com.example.harang.GazeTrackerManager;
import com.example.harang.view.PointView;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    static private List<Time> cData = new ArrayList<>(); //시간정보 저장
    static private ConcentrateManager mInstance = null; //인스턴스
    private final WeakReference<Context> mContext;
    //객체를 다른 액티비티에서 이어서 쓸 수 있지만 생성자로 모두 초기화해서 몇초 동안의 값이 저장되었는지에 대한 정보가 필요함
    static Integer[] concentData = new Integer[7000];
    static Integer[] eyetrackData = new Integer[7000];//나중에 크기를 동적으로 지정할 필요가 있음
    static Integer[] estateData = new Integer[7000]; //null값 사용을 위해
    static Integer[] mstateData = new Integer[7000];

    static int eyeSecondCount = 0; //저장된 시간이 몇초인지 카운트 하기위한 변수
    static int initTimestamp = -1; //초반 타임스탬프 값이 랜덤하게 들어오기 때문에 기준값을 저장하기 위한 변수
    static private int estate= 0; //eyemovement(FIXATION, SACCADE)
    static private int mstate = 0; //screenstate(INSIDE, OUTSIDE)


    //생성자로 액티비티 불러오기
    public ConcentrateManager(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    static public ConcentrateManager makeNewInstance(Context context){ //인스턴스를 처음 사용할 때
        if (mInstance != null) {}
        else{
            mInstance = new ConcentrateManager(context);
        }
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
        
        if(gazeInfo.eyeMovementState == EyeMovementState.FIXATION){
            estate = 1;
        }else{
            estate = 0;
        }
        if(gazeInfo.screenState == ScreenState.INSIDE_OF_SCREEN){
            mstate = 1;
        }else{
            mstate = 0;
        }

        estateData[timestamp - initTimestamp] = estate;
        mstateData[timestamp - initTimestamp] = mstate;

        //overwrite를 하기위해 기존에 배열 해당 인덱스에 값이 존재할 경우 비교
        if(eyetrackData[timestamp - initTimestamp] == null){ //처음 값을 집어넣을 때
            eyetrackData[timestamp - initTimestamp] =estate & mstate; //둘다 1일때만 1
        }else{ //이미 값이 들어있을 때, 한번이라도 0이 나왔으면 0
            if(eyetrackData[timestamp - initTimestamp] == 0 || (estate & mstate) == 0 ){
                eyetrackData[timestamp - initTimestamp] = 0;
            }
        }
        //db에 저장하는 작업 필요******
    }

    //학생 집중정보 테이블 data_concentrate에 들어가는 정보인 전체집중률 c_total, 구간집중률 c_seperate 정보 계산 후 저장
    //연산에 필요한 정보는 영상데이터 테이블 data_video의 전체 러닝시간 v_time,  집중구간1,2 연산을 통해 얻은 집중구간 시간
    //추후 사용자가 영상 시청 후 해당 액티비티가 destroy되기 전 실행
    public void setConcentrate(){
        //1. 전체 집중률
        double c_total = getConcentrateRate(0,eyetrackData.length); //db에 저장하는 작업 필요******

        //2. 구간 집중률
        int clipCount = 2; //클립구간 갯수에 대한 정보 필요********
        int[] concent_start = new int[clipCount];
        int[] concent_end = new int[clipCount];
        double c_seperate = 0; //db에 저장하는 작업 필요******
        for(int i=0;i<clipCount;i++){
            concent_start[i] = 0; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
            concent_end[i] = 10; //db에서 받아오는 작업 필요**********
            c_seperate += getConcentrateRate(concent_start[i],concent_end[i]);
        }
    }

    //startTime ~ endTime 까지 집중 비율 계산
    public double getConcentrateRate(int startTime, int endTime){
        //전체 집중률
        int totalConcent = 0;
        for(int i=startTime;i<=endTime;i++){
            if(eyetrackData[i] == 1){
                totalConcent +=1;
            }
        }
        return  totalConcent/(endTime-startTime);
    }

    //클립영상 구간 추출
    //클립영상 데이터 테이블 data_clipvideo에 들어가는 정보인 집중유지시간 cv_time,
    // 클립영상 시작시간 cv_stime, 클립영상 종료시간 cv_etime 정보 계산 후 저장
    public void setClipvideoData(){
        /*
        db에 저장되어있는 교수자가 입력한 영상에 대한 기록 받아오기
        */
        int clipCount = 2; //클립구간 갯수에 대한 정보 필요********
        int[] concent_start = new int[clipCount];
        int[] concent_end = new int[clipCount];
        for(int i=0;i<clipCount;i++){ //클립영상 구간 받아오기
            concent_start[i] = 0; //db에서 받아오는 작업 필요*********(받아온 후 초단위로 변경)
            concent_end[i] = 10; //db에서 받아오는 작업 필요**********
        }

        /*
        하이라이트 영상 생성
        영상 생성 조건
        전체 영상 길이 : t -> eyeSecondCount
        1. t가 30분 미만이면 k=5초, 이상이면 k=10*(t/1시간) --> concentLint
        30분 = 1800초
        2. 집중시간이 k초 이하인 부분은 ‘집중 안함’으로 변경
        3. ‘집중 안함’값이 가장 오래 연속된 부분을 클립 영상으로 생성(최대 3개)
       */

        //1. k초 설정
        int concentLine = 0; // k가 되는 값. 집중의 기준 시간
        int maintainSec = 0;
        if(eyeSecondCount<1800){
            concentLine = 5;
        }else{
            concentLine = Integer.parseInt(String.valueOf((Double.valueOf(eyeSecondCount)/3600)*10));
        }


        //2. 집중시간이 적은 구간을 집중안함으로 변경하기위한 for문
        for(int i=0;i<eyeSecondCount;i++){ //전체 영상 구간 반복문
            //값을 계속 확인하면서 집중유지시간 체크
            if(eyetrackData[i] == 1){
                maintainSec+=1;
            }else{
                //집중 안함 상태가 되었을 때 집중시간이 k초 미만일 경우 해당구간을 집중안함으로 변경
                if(maintainSec<=concentLine){
                    for(int j=eyeSecondCount-maintainSec;i<eyeSecondCount;i++){
                        eyetrackData[j] = 0;
                    }
                }
                maintainSec = 0;
            }
        }
        //집중안함 값이 가장 오래 연속된 부분 최대 3부분을 클립영상으로 저장 (집중구간 한정으로)
        /*
        리스트?에 시작시간, 유지시간 두가지 정보를 저장한 후 
        유지시간을 기준으로 내림차순 정렬
        위에서부터 3가지 정보가 하이라이트 영상 시간 
        */
        Integer startSec = 0;
        maintainSec = 0;
        HashMap<Integer,Integer> mSecList = new HashMap<Integer,Integer>();
        for(int i=0;i<clipCount;i++){
            for(int j=concent_start[i];j<concent_end[i];j++){ //집중구간 한정으로 for문 돌기 시작
                //값을 계속 확인하면서 집중유지시간 체크
                if(eyetrackData[i] == 1){
                    maintainSec+=1;
                    if(maintainSec == 1){//처음 1일경우 j값을 key에 저장
                        startSec = j;
                        mSecList.put(startSec,maintainSec);
                    }else{ //지속중일경우 sec를 저장
                        mSecList.put(startSec,maintainSec);
                    }
                }else{
                    maintainSec = 0; //집중값이 0이 나왔을 경우 시간 초기화
                }
            }
            startSec = 0;
            maintainSec = 0; //한 집중구간이 끝난 후 누적시간 초기화
        }

        // hashmap에서 value 값이 큰 순서로 정렬

        List<Integer> valueList = new ArrayList<>(mSecList.keySet());
        Collections.sort(valueList, (k1, k2) -> (mSecList.get(k1).compareTo(mSecList.get(k2))));
        // <== 위 연산 결과로 유지시간이 높은 순으로 시작시간이 valueList에 저장됨










    
    }




    public void printTest(){
        for(int i=0;i<eyeSecondCount;i++){
            Log.i("test값", eyetrackData[i]+" "+estateData[i]+" "+mstateData[i]);
        }
    }




    //sharedPreferences로 정보 저장
    public void loadConcentData(){
        /*
       double[] concentData = ConcentDataStorage.loadConcentData(mContext.get());
        if (concentData != null) {
            return 1; //성공
        } else {
            return 0; //실패
        }
        */
    }


}
