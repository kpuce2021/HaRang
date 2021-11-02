package com.example.harangS.activity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoConcentrateRequest extends StringRequest {
    private static String IP = "44.196.58.43"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://" +IP+ "/videoConcentrate.php";
    private Map<String, String> map;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public VideoConcentrateRequest(String v_id, String s_id, HashMap<Integer,Integer> eyetrackData,
                                   HashMap<Integer,Integer> estateData, HashMap<Integer,Integer> mstateData,
                                   int eyeSecondCount, ArrayList<HashMap<String,Integer>> mSecList,
                                   double c_total, double c_seperate, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("vid",v_id);
        map.put("sid", s_id);

        int cvCount = 0;
        if(mSecList.size() < 2){ //2개보다 적을 경우
            cvCount = mSecList.size();
        }else{ //2개 이상일 경우
            cvCount = 2;
            map.put("cvCount","2");
        }
        map.put("cvCount",Integer.toString(cvCount));


        for(int i=0;i<cvCount;i++){
            map.put("cvtime"+i, Integer.toString(mSecList.get(i).get("length")));
            map.put("cvstime"+i, Integer.toString(mSecList.get(i).get("start")));
        }
        Integer temp = Math.toIntExact(Math.round(c_total*100));
        map.put("ctotal",Integer.toString(temp));
        temp = Math.toIntExact(Math.round(c_seperate*100));
        map.put("cseperate",Integer.toString(temp));



        map.put("dtime",Integer.toString(eyeSecondCount));
        for(int i=0;i<eyeSecondCount;i++){
            map.put("concentration"+i, Integer.toString(eyetrackData.get(i)));
            map.put("estate"+i, Integer.toString(estateData.get(i)));
            map.put("mstate"+i, Integer.toString(mstateData.get(i)));
        }

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
