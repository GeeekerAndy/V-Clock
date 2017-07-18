package com.example.dell.v_clock.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王庆伟 on 2017/7/17.
 */

public class MyStringRequest extends StringRequest {
    public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", "application/x-javascript");
        headers.put("Accept-Encoding", "gzip,deflate");
        return headers;
    }
}
