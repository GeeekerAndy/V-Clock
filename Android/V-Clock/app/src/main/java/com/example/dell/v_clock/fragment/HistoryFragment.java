package com.example.dell.v_clock.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    View view;
    ArrayList<GuestHistory> historyArrayList;
    MessageHistoryAdapter adapter;
    RequestQueue requestQueue;
    String eid;
    ListView historyList;

    int page = 1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        page = 1;
        SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);
        Log.d("TAG", "eid = " + eid);

        historyArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        JSONObjectRequestMapParams jsonObjectRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Get visiting history from JSONObject and add to arrayList here.
                        try {
                            JSONArray jsonArray = response.getJSONArray(ServerInfo.VISITING_RECORD_KEY);
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
                                        jsonObject.getString("arrivingdate"),
                                        jsonObject.getString("gphoto"));
                                Log.d("TAG", guestHistory.getGuestName() + guestHistory.getArriveTime());
                                historyArrayList.add(guestHistory);
                                Log.d("History", guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
                            }
                            adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                            historyList = view.findViewById(R.id.lv_history_list);
                            historyList.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("ERROR", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> eidMap= new HashMap<>();
                eidMap.put("page", page + "");
                eidMap.put("eid", eid);
                return eidMap;
            }
        };

        final SwipyRefreshLayout refreshHistoryFromBottom = view.findViewById(R.id.srl_refresh_history_from_bottom);
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
                                    if(jsonArray.length() > 0) {
                                        for(int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            GuestHistory guestHistory = new GuestHistory(jsonObject.getString("gname"),
                                                    jsonObject.getString("arrivingdate"),
                                                    jsonObject.getString("gphoto"));
                                            Log.d("TAG", guestHistory.getGuestName() + guestHistory.getArriveTime());
                                            historyArrayList.add(guestHistory);
                                            Log.d("History", guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
                                        }
                                        if(historyList.getAdapter() == null) {
                                            adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                                            historyList = view.findViewById(R.id.lv_history_list);
                                        } else {
                                            adapter.notifyDataSetChanged();
                                        }
                                        page++;
                                    } else if(jsonArray.length() == 0) {
                                        Toast.makeText(getContext(), "这是最后一页啦！", Toast.LENGTH_SHORT).show();
                                    }
                                    refreshHistoryFromBottom.setRefreshing(false);
                                } catch (JSONException e) {
                                    Log.e("ERROR", e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                Log.e("ERROR", error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> eidMap= new HashMap<>();
                        eidMap.put("page", page + "");
                        eidMap.put("eid", eid);
                        return eidMap;
                    }
                };
                requestQueue.add(jsonObjectRequestOnRefresh);
            }
        });

        requestQueue.add(jsonObjectRequest);
        Log.d("TAG", "onCreate in history fragment");
        return view ;
    }

    @Override
    public void onStop() {
        super.onStop();
        requestQueue.stop();
    }


}

