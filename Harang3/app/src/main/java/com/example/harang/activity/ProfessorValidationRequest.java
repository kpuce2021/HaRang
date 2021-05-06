package com.example.harang.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfessorValidationRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    private static String IP = "3.214.234.24"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.

    final static private String URL = "http://" +IP+ "/p_validation.php";
    private Map<String, String> map;


    public ProfessorValidationRequest(String id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id",id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}