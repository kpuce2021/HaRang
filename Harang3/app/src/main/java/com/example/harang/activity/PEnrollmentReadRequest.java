package com.example.harang.activity;

import android.os.Build;
import android.util.Log;


import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PEnrollmentReadRequest extends StringRequest {
    private static String IP = "3.214.234.24"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://" +IP+ "/enrollmentRead.php";
    private static HashMap<String, String> map;

    public PEnrollmentReadRequest(String p_id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("p_id",p_id);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
