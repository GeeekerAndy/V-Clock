package com.example.dell.v_clock.fragment;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import com.example.dell.v_clock.adapter.MessageListAdapter;
import com.example.dell.v_clock.object.GuestMessage;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;
import com.example.dell.v_clock.util.SwipeDismissListViewTouchListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * The fragment displays one message of a guest when he/she arrive.
 * 当一位嘉宾到达后，该碎片显示一条到达信息。
 */
public class MessageListFragment extends Fragment {

    final String TAG = "MessageList";

    MessageDBHelper dbHelper;
    MessageListAdapter messageListAdapter;
    ContentValues messageValues;
    SQLiteDatabase db;
    View layoutView;
    List<GuestMessage> guestMessageList;
    SwipeRefreshLayout refreshLayout;
//    MessageBroadCastReceiver broadCastReceiver;
    String session_id;

    public MessageListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbHelper = new MessageDBHelper(getContext());
        messageValues = new ContentValues();
        guestMessageList = new ArrayList<>();
        layoutView = inflater.inflate(R.layout.fragment_message, container, false);

        SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        session_id = sp.getString("eid", null);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("MESSAGE_ARRIVE_BROADCAST");
//        broadCastReceiver = new MessageBroadCastReceiver();
//        getContext().registerReceiver(broadCastReceiver, intentFilter);

        refreshLayout = layoutView.findViewById(R.id.srl_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                guestMessageList.clear();
                GuestMessage[] guestMessages = new restoreMessageList().doInBackground();
                for (int i = 0; i < guestMessages.length; i++) {
                    guestMessageList.add(guestMessages[i]);
                }
                messageListAdapter = new MessageListAdapter(getContext(), R.layout.one_message_in_list, guestMessageList);
                ListView guestMessageView = layoutView.findViewById(R.id.lv_message_list);
                SwipeDismissListViewTouchListener touchListener =
                        new SwipeDismissListViewTouchListener(
                                guestMessageView,
                                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                    @Override
                                    public boolean canDismiss(int position) {
                                        return true;
                                    }

                                    @Override
                                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                        for (int position : reverseSortedPositions) {
                                            String[] visitInfo = {messageListAdapter.getItem(position).getGuestName(),
                                                    messageListAdapter.getItem(position).getArriveTime()};
                                            new removeOneMessage().execute(visitInfo);
                                            messageListAdapter.remove(messageListAdapter.getItem(position));
                                        }
                                        messageListAdapter.notifyDataSetChanged();
                                    }
                                });
                guestMessageView.setOnTouchListener(touchListener);
                guestMessageView.setAdapter(messageListAdapter);
                refreshLayout.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment;
        return layoutView;
    }

    @Override
    public void onStart() {


        //Restore unread Message
        //恢复唯独未读消息
        GuestMessage[] guestMessages = new restoreMessageList().doInBackground();
        guestMessageList.clear();
        for (int i = 0; i < guestMessages.length; i++) {
            guestMessageList.add(guestMessages[i]);
        }
        messageListAdapter = new MessageListAdapter(getContext(), R.layout.one_message_in_list, guestMessageList);
        ListView guestMessageView = layoutView.findViewById(R.id.lv_message_list);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        guestMessageView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String[] visitInfo = {messageListAdapter.getItem(position).getGuestName(),
                                            messageListAdapter.getItem(position).getArriveTime()};
                                    new removeOneMessage().execute(visitInfo);
                                    messageListAdapter.remove(messageListAdapter.getItem(position));
                                }
                                messageListAdapter.notifyDataSetChanged();
                            }
                        });
        guestMessageView.setOnTouchListener(touchListener);
        guestMessageView.setAdapter(messageListAdapter);
        refreshLayout.setRefreshing(false);
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
//        getContext().unregisterReceiver(broadCastReceiver);
        super.onDestroy();
    }

    private class removeOneMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... visitInfo) {
            db = dbHelper.getWritableDatabase();
            if (visitInfo.length < 1) {
                Log.d(TAG, "数据库删除失败！嘉宾信息不完整");
            } else {
                String selection = VClockContract.MessageInfo.COLUMN_NAME_GNAME + "=? and " +
                        VClockContract.MessageInfo.COLUMN_NAME_DATE + "=?";
                String[] selectionArgs = {visitInfo[0], visitInfo[1]};
                db.delete(VClockContract.MessageInfo.TABLE_NAME, selection, selectionArgs);
                Log.d(TAG, "删除一条消息, 嘉宾姓名：" + visitInfo[0] + "，到访时间：" + visitInfo[1]);
            }
            return null;
        }
    }

    private class restoreMessageList extends AsyncTask<Void, Void, GuestMessage[]> {
        @Override
        protected GuestMessage[] doInBackground(Void... voids) {
            db = dbHelper.getReadableDatabase();
            String[] projection = {
                    VClockContract.MessageInfo.COLUMN_NAME_GNAME,
                    VClockContract.MessageInfo.COLUMN_NAME_DATE
            };
            Cursor cursor = db.query(
                    VClockContract.MessageInfo.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            GuestMessage[] guestMessages = new GuestMessage[cursor.getCount()];
            while (cursor.moveToNext()) {
                String guestName = cursor.getString(0);
                String arriveTime = cursor.getString(1);
                GuestMessage guestMessage = new GuestMessage(guestName, arriveTime);
                guestMessages[cursor.getPosition()] = guestMessage;
            }
            cursor.close();
            return guestMessages;
        }
    }

//    private class UpdateHistory extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//            JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            if(response != null) {
//                                try {
//                                    synchronized (this) {
//                                        SharedPreferences.Editor editor = getContext().getSharedPreferences("history", MODE_PRIVATE).edit();
//                                        editor.putString("page", response.toString());
//                                        editor.apply();
//                                    }
//                                } catch (OutOfMemoryError e) {
//                                    Log.e(TAG, e.getMessage());
//                                } catch (NullPointerException e) {
//                                    Log.d(TAG, e.getMessage());
//                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
////                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> eidMap = new HashMap<>();
//                    eidMap.put("page", "1");
//                    eidMap.put("eid", session_id);
//                    return eidMap;
//                }
//            };
//            requestQueue.add(jsonObjectRequest);
//            return null;
//        }
//    }

//    public class MessageBroadCastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String gname = intent.getStringExtra("gname");
//            Intent checkMessageIntent = new Intent(context, MainActivity.class);
//            checkMessageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, checkMessageIntent, 0);
//            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new NotificationCompat.Builder(getContext())
//                    .setContentTitle("一位嘉宾到访")
//                    .setContentText("嘉宾" + gname + "到达")
//                    .setAutoCancel(true)
//                    .setSmallIcon(R.drawable.ic_person_pin_circle_white_36dp)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_logo))
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setContentIntent(pendingIntent)
//                    .build();
//            manager.notify(1, notification);
//
//            new UpdateHistory().doInBackground();
//        }
//    }
}
