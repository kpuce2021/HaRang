package com.example.harangS.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfessorRegisterRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    private static String IP = "100.26.4.92"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.

    final static private String URL = "http://" +IP+ "/p_register.php";
    private Map<String, String> map;


    public ProfessorRegisterRequest(String id, String password, String p_name, String p_major, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id",id);
        map.put("password", password);
        map.put("p_name", p_name);
        map.put("p_major", p_major);
        //map.put("userAge", userAge + "");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}