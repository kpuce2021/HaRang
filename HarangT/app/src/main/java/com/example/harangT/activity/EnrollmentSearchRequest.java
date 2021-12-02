package com.example.harangT.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EnrollmentSearchRequest extends StringRequest {
    private static String IP = "44.196.58.43"; //서버 없이 사용하는 IP가 있다면 저장해서 사용하면 된다.
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://" +IP+ "/enrollmentSearch.php";
    private static HashMap<String, String> map;

    public EnrollmentSearchRequest(String p_id, String flag, String searchString, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("p_id",p_id);
        map.put("flag",flag);
        map.put("searchString",searchString);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}