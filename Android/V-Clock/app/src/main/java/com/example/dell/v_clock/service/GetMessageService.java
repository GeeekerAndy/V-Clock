package com.example.dell.v_clock.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.MessageDBHelper;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.VClockContract;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.adapter.MessageHistoryAdapter;
import com.example.dell.v_clock.object.GuestHistory;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;
import com.example.dell.v_clock.util.JSONObjectRequestWithSessionID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetMessageService extends Service {

    final String TAG = "GetMessageService";

    RequestQueue requestQueue;
    SQLiteDatabase db;
    String session_id;
    MessageBroadCastReceiver broadCastReceiver;

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
        Log.d(TAG, "service onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MESSAGE_ARRIVE_BROADCAST");
        broadCastReceiver = new MessageBroadCastReceiver();
        registerReceiver(broadCastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service onStartCommand");
        //Polling to obtain server information
        //轮询获取服务器嘉宾到访信息
        requestQueue = Volley.newRequestQueue(getBaseContext());
        SharedPreferences sp = getBaseContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        session_id = sp.getString("eid", null);

        final JSONObjectRequestWithSessionID jsonObjectRequest = new JSONObjectRequestWithSessionID(Request.Method.GET, ServerInfo.PUSH_MESSAGE_URL + "?eid=" + session_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Json object example {"eid":4,"gname":12,"arrivingDate":1234}
                        db = new MessageDBHelper(getBaseContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        try {
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_GNAME, response.getString("gname"));
                            values.put(VClockContract.MessageInfo.COLUMN_NAME_DATE, response.getString("arrivingDate"));
                            Intent broadcastIntent = new Intent("MESSAGE_ARRIVE_BROADCAST");
                            broadcastIntent.putExtra("gname", response.getString("gname"));
                            sendBroadcast(broadcastIntent);
                            Log.d(TAG, "嘉宾到达：" + response.getString("gname"));
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        db.insert(VClockContract.MessageInfo.TABLE_NAME, null, values);
//                        Toast.makeText(getBaseContext(), "服务器返回" + response.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "服务器返回" + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "响应错误：服务器超时或没有嘉宾消息");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    Log.d(TAG, "发送消息请求");
                    requestQueue.add(jsonObjectRequest);
                    SystemClock.sleep(5 * 1000);
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (requestQueue != null) {
            requestQueue.stop();
        }
        if (db != null) {
            db.close();
        }
        unregisterReceiver(broadCastReceiver);
        super.onDestroy();
    }

    public class MessageBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String gname = intent.getStringExtra("gname");
            Intent checkMessageIntent = new Intent(context, MainActivity.class);
            checkMessageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, checkMessageIntent, 0);
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(GetMessageService.this)
                    .setContentTitle("一位嘉宾到访")
                    .setContentText("嘉宾" + gname + "到达")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_person_pin_circle_white_36dp)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_logo))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .build();
            manager.notify(1, notification);
            new UpdateHistory().doInBackground();
        }
    }

    public class UpdateHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response != null) {
                                try {
                                    SharedPreferences.Editor editor = getSharedPreferences("history", MODE_PRIVATE).edit();
                                    editor.putString("page", response.toString());
                                    editor.apply();
                                } catch (OutOfMemoryError e) {
                                    Log.e(TAG, "Out of memory.");
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> eidMap = new HashMap<>();
                    eidMap.put("page", "1");
                    eidMap.put("eid", session_id);
                    return eidMap;
                }
            };
            requestQueue.add(jsonObjectRequest);
            return null;
        }
    }

}
