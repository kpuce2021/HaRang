package com.example.harangS.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StudentAllTypeVideoListRequset extends StringRequest {
    private static String IP = "100.26.4.92"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://" +IP+ "/s_readAllTypeVideoList.php";
    private static HashMap<String, String> map;

    public StudentAllTypeVideoListRequset(String s_id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("s_id",s_id);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
