package com.example.dell.v_clock.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.MessageDBHelper;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.VClockContract;

import org.json.JSONObject;

public class getMessageService extends Service {
    public getMessageService() {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    
                    RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ServerInfo.PUSH_MESSAGE_URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            SQLiteDatabase db = new MessageDBHelper(getBaseContext()).getWritableDatabase();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TAG", "服务器超时或没有嘉宾消息");
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        SQLiteDatabase db = new MessageDBHelper(getBaseContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VClockContract.MessageInfo.COLUMN_NAME_GNAME, "小1");
        values.put(VClockContract.MessageInfo.COLUMN_NAME_DATE, "2017.07.07 23:45:22");
        db.insert(VClockContract.MessageInfo.TABLE_NAME, null, values);
        return super.onStartCommand(intent, flags, startId);
    }
}
