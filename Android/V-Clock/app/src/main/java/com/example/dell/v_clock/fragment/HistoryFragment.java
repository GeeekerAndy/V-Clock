package com.example.dell.v_clock.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.adapter.MessageHistoryAdapter;
import com.example.dell.v_clock.object.GuestHistory;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * This fragment shows the visitation history of one's guests.
 * 这个碎片展示了一个工作人员接待的嘉宾到访记录。
 */
public class HistoryFragment extends Fragment {

    final String TAG = "HistoryFragment";

    View view;
    ArrayList<GuestHistory> historyArrayList;
    MessageHistoryAdapter adapter;
    RequestQueue requestQueue;
    String eid;
    ListView historyList;
    ImageButton updateHistory;

    int page = 1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        page = 2;
        SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);
        Log.d(TAG, "eid = " + eid);
        historyArrayList = new ArrayList<>();
        historyList = view.findViewById(R.id.lv_history_list);
        final SwipyRefreshLayout refreshHistoryFromBottom = view.findViewById(R.id.srl_refresh_history_from_bottom);

        final RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        updateHistory = view.findViewById(R.id.ibt_update_history);
        updateHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHistory.startAnimation(rotate);
                updateHistory.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_refresh_white_24px));
                new UpdateHistory().doInBackground();
            }
        });

        requestQueue = Volley.newRequestQueue(getContext());
        new RestoreHistory().doInBackground();
//        JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //Get visiting history from JSONObject and add to arrayList here.
//                        try {
//                            page++;
//                            JSONArray jsonArray = response.getJSONArray(ServerInfo.VISITING_RECORD_KEY);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
//                                        jsonObject.getString("arrivingdate"),
//                                        jsonObject.getString("gphoto"));
//                                Log.d(TAG, guestHistory.getGuestName() + guestHistory.getArriveTime());
//                                historyArrayList.add(guestHistory);
//                                Log.d(TAG, guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
//                            }
//                            adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
//                            historyList.setAdapter(adapter);
//                        } catch (JSONException e) {
//                            Log.e(TAG, e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> eidMap = new HashMap<>();
//                eidMap.put("page", page + "");
//                eidMap.put("eid", eid);
//                return eidMap;
//            }
//        };
//        requestQueue.add(jsonObjectRequest);

//        SharedPreferences historyPreferences = getContext().getSharedPreferences("history", MODE_PRIVATE);
//        String historyString = historyPreferences.getString("page", null);
//        try {
//            if(historyString == null) {
//
//            } else {
//                JSONObject JSONHistory = new JSONObject(historyString);
//                JSONArray jsonArray = JSONHistory.getJSONArray(ServerInfo.VISITING_RECORD_KEY);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
//                            jsonObject.getString("arrivingdate"),
//                            jsonObject.getString("gphoto"));
//                    Log.d(TAG, guestHistory.getGuestName() + guestHistory.getArriveTime());
//                    historyArrayList.add(guestHistory);
//                    Log.d(TAG, guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
//                }
//                adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
//                historyList.setAdapter(adapter);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, e.getMessage());
//        }

        refreshHistoryFromBottom.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                JSONObjectRequestMapParams jsonObjectRequestOnRefresh = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Get visiting history from JSONObject and add to arrayList here.
                                try {
                                    JSONArray jsonArray = response.getJSONArray(ServerInfo.VISITING_RECORD_KEY);
                                    if (jsonArray.length() > 0) {
                                        page++;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
                                                    jsonObject.getString("arrivingdate"),
                                                    jsonObject.getString("gphoto"));
                                            Log.d(TAG, guestHistory.getGuestName() + guestHistory.getArriveTime());
                                            historyArrayList.add(guestHistory);
                                            Log.d(TAG, guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
                                        }
                                        if (historyList.getAdapter() == null) {
                                            adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                                            historyList.setAdapter(adapter);
                                        } else {
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else if (jsonArray.length() == 0) {
                                        Toast.makeText(getContext(), "这是最后一页啦！", Toast.LENGTH_SHORT).show();
                                    }
                                    refreshHistoryFromBottom.setRefreshing(false);
                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(getContext(), "服务器错误！", Toast.LENGTH_SHORT).show();
                            refreshHistoryFromBottom.setRefreshing(false);
                            Log.e(TAG, error.getMessage());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> eidMap = new HashMap<>();
                        eidMap.put("page", page + "");
                        eidMap.put("eid", eid);
                        return eidMap;
                    }
                };
                requestQueue.add(jsonObjectRequestOnRefresh);
            }
        });
        Log.d(TAG, "onCreate in history fragment");
        return view;
    }

    private class UpdateHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
//            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response != null) {
                                try {
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("history", MODE_PRIVATE).edit();
                                    editor.putString("page", response.toString());
                                    editor.apply();
                                } catch (OutOfMemoryError e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                new RestoreHistory().doInBackground();
                                updateHistory.clearAnimation();
                                updateHistory.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_cloud_done_white_24px));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    updateHistory.clearAnimation();
                    try {
                        updateHistory.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_cloud_off_white_24px));
                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    }
//                Toast.makeText(getContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> eidMap = new HashMap<>();
                    eidMap.put("page", "1");
                    eidMap.put("eid", eid);
                    return eidMap;
                }
            };
            requestQueue.add(jsonObjectRequest);
            return null;
        }
    }

    private class RestoreHistory extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... params) {
            SharedPreferences historyPreferences = getContext().getSharedPreferences("history", MODE_PRIVATE);
            String historyString = historyPreferences.getString("page", null);
            try {
                if (historyString == null) {

                } else {
                    JSONObject JSONHistory = new JSONObject(historyString);
                    JSONArray jsonArray = JSONHistory.getJSONArray(ServerInfo.VISITING_RECORD_KEY);
                    historyArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
                                jsonObject.getString("arrivingdate"),
                                jsonObject.getString("gphoto"));
                        Log.d(TAG, guestHistory.getGuestName() + guestHistory.getArriveTime());
                        historyArrayList.add(guestHistory);
                        adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                        Log.d(TAG, guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                        historyList.setAdapter(adapter);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        requestQueue.stop();
    }
}

