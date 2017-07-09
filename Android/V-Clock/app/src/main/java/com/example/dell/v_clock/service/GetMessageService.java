package com.example.dell.v_clock.service;

import android.app.Application;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateIntervalFormat;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.MessageDBHelper;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.VClockContract;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.fragment.MessageListFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GetMessageService extends Service {

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";

    public GetMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "service onStartCommand");
        //Polling to obtain server information
        //轮询获取服务器嘉宾到访信息
        final RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        SharedPreferences sp = getBaseContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
//        String session_id = sp.getString("eid", null);
        String session_id = "0004";
        final CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.GET, ServerInfo.PUSH_MESSAGE_URL + "?eid=0004", null, session_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Json object example {"eid":4,"gname":12,"arrivingDate":1234}
                        SQLiteDatabase db = new MessageDBHelper(getBaseContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        try {
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_GNAME, response.getString("gname"));
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_DATE, response.getString("arrivingDate"));
                        } catch (JSONException e) {
                            Log.e("TAG", e.getMessage());
                        }
                        db.insert(VClockContract.MessageInfo.TABLE_NAME, null, values);
                        Toast.makeText(getBaseContext(), "服务器返回" + response.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "服务器返回" + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "响应错误：服务器超时或没有嘉宾消息");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
//                    requestQueue.add(jsonObjectRequest);
                    SystemClock.sleep(5000);
                }
            }
        }).start();

//        SQLiteDatabase db = new MessageDBHelper(getBaseContext()).getWritableDatabase();
//        ContentValues values = new ContentValues();
//        for(int i = 0; i < 4; i++) {
//            values.put(VClockContract.MessageInfo.COLUMN_NAME_GNAME, "小" + i);
//            values.put(VClockContract.MessageInfo.COLUMN_NAME_DATE, "2017.07.01 00:00");
//            db.insert(VClockContract.MessageInfo.TABLE_NAME, null, values);
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class CustomRequest extends JsonObjectRequest {

        private String session_id = "";

        public CustomRequest(int method, String url, JSONObject jsonRequest,
                             Response.Listener listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public CustomRequest(int method, String url, JSONObject jsonRequest, String session_id,
                             Response.Listener listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            this.session_id = session_id;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();
            Log.d("TAG", "getHeaders() session_id = " + session_id);
            if (headers == null
                    || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<>();
            }
            if (this.session_id.length() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(SESSION_COOKIE);
                builder.append("=");
                builder.append(this.session_id);
                if (headers.containsKey(COOKIE_KEY)) {
                    builder.append("; ");
                    builder.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder.toString());
            }
            return headers;
        }
    }
}
