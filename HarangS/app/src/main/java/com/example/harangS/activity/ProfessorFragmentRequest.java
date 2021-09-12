package com.example.harangS.activity;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfessorFragmentRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    private static String IP = "100.26.4.92"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.

    final static private String URL = "http://" +IP+ "/uploadvideo.php";
    private Map<String, String> map;


    public ProfessorFragmentRequest(String p_id, String V_name, String V_textfield, String V_Calendar, String V_concent1_start, String V_concent1_stop, String V_concent2_start, String V_concent2_stop, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        Log.d("dbTestFragment",p_id);
        Log.d("dbTestFragment",V_name);
        Log.d("dbTestFragment",V_textfield);
        Log.d("dbTestFragment",V_Calendar);
        Log.d("dbTestFragment",V_concent1_start);
        Log.d("dbTestFragment",V_concent1_stop);
        Log.d("dbTestFragment",V_concent2_start);
        Log.d("dbTestFragment",V_concent2_stop);

       // map.put("v_id",V_id);
        map.put("p_id",p_id);
        map.put("v_name",V_name);
        //map.put("v_time",V_time);
        map.put("v_textfield", V_textfield);
        map.put("v_calendar", V_Calendar);
        map.put("v_concent1_start", V_concent1_start);
        map.put("v_concent1_stop", V_concent1_stop);
        map.put("v_concent2_start", V_concent2_start);
        map.put("v_concent2_stop", V_concent2_stop);
        map.put("v_attend", "0");
        map.put("v_concent", "0");
}
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
