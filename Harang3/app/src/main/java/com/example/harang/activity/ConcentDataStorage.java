package com.example.harang.activity;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConcentDataStorage {
    private static String IP = "3.214.234.24"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://" +IP+ "/videoConcentrate.php";
    private static HashMap<String, String> map;



    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initConcentDataStorage(String v_id, String s_id, HashMap<Integer,Integer> eyetrackData,
                                              HashMap<Integer,Integer> estateData, HashMap<Integer,Integer> mstateData,
                                              int eyeSecondCount, ArrayList<HashMap<String,Integer>> mSecList, double c_total, double c_seperate, Context mContext){

        Log.i("db_contest","cds 진입");
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            /*
                            Log.i("db_contest", "cvCount : " +  jsonObject.getString("cvCount")+", d_time : "+ jsonObject.getString("d_time"));
                            Log.i("db_contest", "v_id : " +  jsonObject.getString("v_id")+", s_id : "+ jsonObject.getString("s_id"));
                            Log.i("db_contest", "c_total : " +  jsonObject.getString("c_total")+", c_seperate : "+ jsonObject.getString("c_seperate"));
                            for(int i=0;i< Integer.parseInt(jsonObject.getString("cvCount"));i++){
                                Log.i("db_contest", "cv_time"+i+" : " +  jsonObject.getString("cv_time"+i) + ", cv_stime"+i+" : " +  jsonObject.getString("cv_stime"+i));
                            }
                            for(int i=0;i<Integer.parseInt(jsonObject.getString("d_time"));i++){
                                Log.i("db_contest", "concentration"+i+" : " +  jsonObject.getString("concentration"+i) + ", e_state"+i+" : " +  jsonObject.getString("e_state"+i)+ ", m_state"+i+" : " +  jsonObject.getString("m_state"+i));
                            }
                               */
                            Log.i("db_contest", "mysql error : " + jsonObject.getString("error"));
//
                        } else {
                            Log.i("db_contest", "mysql error : " + jsonObject.getString("error"));
                            Log.i("db_contest", "server connect failllll!!!!!!");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("db_contest", "catch : " + e.getMessage());
                    }
                }
            };


            VideoConcentrateRequest vcr = new VideoConcentrateRequest(v_id, s_id, eyetrackData, estateData, mstateData,
                    eyeSecondCount, mSecList, c_total, c_seperate, responseListener);
            vcr.setShouldCache(false);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(vcr);
    }

}
