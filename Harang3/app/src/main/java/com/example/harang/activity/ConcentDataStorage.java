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
    private static int flag = 1;



    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initConcentDataStorage(String v_id, String s_id, HashMap<Integer,Integer> eyetrackData,
                                              HashMap<Integer,Integer> estateData, HashMap<Integer,Integer> mstateData,
                                              int eyeSecondCount, ArrayList<HashMap<String,Integer>> mSecList, double c_total, double c_seperate, Context mContext){

        Log.i("db_test","cds 진입");
        if(flag == 1) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            //Log.i("db_test", "what : " + jsonObject.getString("what"));
                            Log.i("db_test", "mysql error : " + jsonObject.getString("error"));
//                            Log.i("db_test", "test0 : " + jsonObject.getString("test0"));
                        } else {
                            Log.i("db_test", "server connect failllll!!!!!!");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("db_test", "catch : " + e.getMessage());
                    }
                }
            };


            VideoConcentrateRequest vcr = new VideoConcentrateRequest(v_id, s_id, eyetrackData, estateData, mstateData,
                    eyeSecondCount, mSecList, c_total, c_seperate, responseListener);
            vcr.setShouldCache(false);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(vcr);
        }
        flag = 0;
    }

}
