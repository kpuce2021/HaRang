package com.example.harangS.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StudentRegisterRequest extends StringRequest {
    private static String IP = "44.196.58.43";
    final static private String URL = "http://" +IP+ "/s_register.php";
    private Map<String, String> map;


    public StudentRegisterRequest(String id, String password, String s_name, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id",id);
        map.put("password", password);
        map.put("s_name", s_name);
        //map.put("userAge", userAge + "");

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}