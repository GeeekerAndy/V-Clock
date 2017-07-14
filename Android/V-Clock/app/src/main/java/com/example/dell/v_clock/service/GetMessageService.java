package com.example.dell.v_clock.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.MessageDBHelper;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.VClockContract;
import com.example.dell.v_clock.util.JSONObjectRequestWithSessionID;

import org.json.JSONException;
import org.json.JSONObject;

public class GetMessageService extends Service {

    RequestQueue requestQueue;

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
        requestQueue = Volley.newRequestQueue(getBaseContext());
        SharedPreferences sp = getBaseContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        String session_id = sp.getString("eid", null);

        final JSONObjectRequestWithSessionID jsonObjectRequest = new JSONObjectRequestWithSessionID(Request.Method.GET, ServerInfo.PUSH_MESSAGE_URL + "?eid="+session_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Json object example {"eid":4,"gname":12,"arrivingDate":1234}
                        SQLiteDatabase db = new MessageDBHelper(getBaseContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        try {
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_GNAME, response.getString("gname"));
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_DATE, response.getString("arrivingDate"));
                            Intent broadcastIntent = new Intent("MESSAGE_ARRIVE_BROADCAST");
                            broadcastIntent.putExtra("gname", response.getString("gname"));
                            sendBroadcast(broadcastIntent);
                        } catch (JSONException e) {
                            Log.e("TAG", e.getMessage());
                        }
                        db.insert(VClockContract.MessageInfo.TABLE_NAME, null, values);
//                        Toast.makeText(getBaseContext(), "服务器返回" + response.toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("TAG", "发送消息请求");
//                    requestQueue.add(jsonObjectRequest);
                    SystemClock.sleep(5*1000);
                }
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        requestQueue.stop();
        super.onDestroy();
    }
}
