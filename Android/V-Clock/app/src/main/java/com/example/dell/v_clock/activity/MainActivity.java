package com.example.dell.v_clock.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.fragment.GuestListFragment;
import com.example.dell.v_clock.fragment.HistoryFragment;
import com.example.dell.v_clock.fragment.MeFragment;
import com.example.dell.v_clock.fragment.MessageListFragment;
import com.example.dell.v_clock.service.GetMessageService;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

/**
 * This is the main interface, including four parts Messages, GuestList, History and Me.
 * 这是程序的主界面, 包括消息，嘉宾列表，到达记录和我的四个部分。
 */

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    final MessageListFragment messageListFragment = new MessageListFragment();
    final GuestListFragment guestListFragment = new GuestListFragment();
    final HistoryFragment historyFragment = new HistoryFragment();
    final MeFragment meFragment = new MeFragment();
//    MessageBroadCastReceiver broadCastReceiver;
    String session_id;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notifications:
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.ll_fragment_container, messageListFragment);
                    transaction1.commit();
                    return true;
                case R.id.navigation_guests:
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.ll_fragment_container, guestListFragment);
                    transaction2.commit();
                    return true;
                case R.id.navigation_history:
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.ll_fragment_container, historyFragment);
                    transaction3.commit();
                    return true;
                case R.id.navigation_me:
                    FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                    transaction4.replace(R.id.ll_fragment_container, meFragment);
                    transaction4.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getBaseContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        session_id = sp.getString("eid", null);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ll_fragment_container, messageListFragment);
        transaction.commit();

        Intent startServiceIntent = new Intent(this, GetMessageService.class);
        startService(startServiceIntent);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("MESSAGE_ARRIVE_BROADCAST");
//        broadCastReceiver = new MessageBroadCastReceiver();
//        registerReceiver(broadCastReceiver, intentFilter);

        SharedPreferences historyPreferences = getSharedPreferences("history", MODE_PRIVATE);
        String historyString = historyPreferences.getString("page", null);
        if(historyString == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new UpdateHistory().doInBackground();
                }
            }).start();
        }
    }

    private class UpdateHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response != null) {
                                SharedPreferences.Editor editor = getSharedPreferences("history", MODE_PRIVATE).edit();
                                editor.putString("page", response.toString());
                                editor.apply();
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

//    public class MessageBroadCastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String gname = intent.getStringExtra("gname");
//            Intent checkMessageIntent = new Intent(context, MainActivity.class);
//            checkMessageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, checkMessageIntent, 0);
//            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new NotificationCompat.Builder(MainActivity.this)
//                    .setContentTitle("一位嘉宾到访")
//                    .setContentText("嘉宾" + gname + "到达")
//                    .setAutoCancel(true)
//                    .setSmallIcon(R.drawable.ic_person_pin_circle_white_36dp)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_logo))
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setContentIntent(pendingIntent)
//                    .build();
//            manager.notify(1, notification);
//            new UpdateHistory().doInBackground();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
