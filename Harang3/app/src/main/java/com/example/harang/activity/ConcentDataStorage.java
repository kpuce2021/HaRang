package com.example.harang.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Arrays;
/*
* 학생 집중 정보 테이블 data_concentrate 정보를 db에 저장하기 전 임시로 디바이스 내부에 저장
* 집중정보 아이디 제외 다른정보를 계산 후 저장
*
*
* */
/* 나중에 이곳에서 클립영상 데이터도 같이 수행 - 집중 구간 계산 관련*/
public class ConcentDataStorage {
    private static final String TAG = ConcentDataStorage.class.getSimpleName();
    private static final String CONCENT_DATA = "concentData";

    /*
    임시로 sharedPreferences에 저장
    * 학생 집중 정보 테이블 data_concentrate에 저장되는 정보
    double[] concentData에  v_id부터 순서대로 저장
    * c_id - 집중 정보 아이디
    * v_id - 영상 아이디
    * s_id - 학습자 아이디
    * c_total - 전체 집중률
    * c_seperate - 구간 집중률
    *
    * */

    /* sharedPreferences에 정보 저장 */
    public static void saveConcneData(Context context, double[] concentData){
        if(concentData !=null && concentData.length>0) {
            SharedPreferences.Editor editor = context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit(); //신규 editor 생성
            editor.putString(CONCENT_DATA, Arrays.toString(concentData));
            editor.apply();
        } else{
            Log.e(TAG, "집중 정보 없음 오류");
        }
    }


    /*
    아래 순서로 정보 불러짐
    * v_id - 영상 아이디
    * s_id - 학습자 아이디
    * c_total - 전체 집중률
    * c_seperate - 구간 집중률
    *
    * */
    /* sharedPreferences에서 정보 로드 */
    public static @Nullable
    double[] loadConcentData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String saveData = prefs.getString(CONCENT_DATA, null);

        if (saveData != null) {
            try {
                String[] split = saveData.substring(1, saveData.length() - 1).split(", ");
                double[] array = new double[split.length];
                for (int i = 0; i < split.length; i++) {
                    array[i] = Double.parseDouble(split[i]);
                }
                return array;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "집중 정보 형식이 맞지 않을 때 오류");
            }
        }
        return null;


    }
}
