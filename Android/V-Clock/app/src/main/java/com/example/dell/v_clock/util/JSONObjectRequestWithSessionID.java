package com.example.dell.v_clock.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andy on 7/10/17.
 */

public class JSONObjectRequestWithSessionID extends JsonObjectRequest {

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";
    private String session_id = "";

    public JSONObjectRequestWithSessionID(int method, String url, JSONObject jsonRequest,
                                          Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JSONObjectRequestWithSessionID(int method, String url, JSONObject jsonRequest, String session_id,
                                          Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.session_id = session_id;
    }

//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        Map<String, String> headers = super.getHeaders();
//        Log.d("TAG", "getHeaders() session_id = " + session_id);
//        if (headers == null
//                || headers.equals(Collections.emptyMap())) {
//            headers = new HashMap<>();
//        }
//        if (this.session_id.length() > 0) {
//            StringBuilder builder = new StringBuilder();
//            builder.append(SESSION_COOKIE);
//            builder.append("=");
//            builder.append(this.session_id);
//            if (headers.containsKey(COOKIE_KEY)) {
//                builder.append("; ");
//                builder.append(headers.get(COOKIE_KEY));
//            }
//            headers.put(COOKIE_KEY, builder.toString());
//        }
//        return headers;
//    }
}
