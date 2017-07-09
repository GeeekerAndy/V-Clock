package com.example.dell.v_clock.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.adapter.MessageHistoryAdapter;
import com.example.dell.v_clock.object.GuestHistory;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This fragment shows the visitation history of one's guests.
 * 这个碎片展示了一个工作人员接待的嘉宾到访记录。
 */
public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        final ArrayList<GuestHistory> historyArrayList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Get visiting history from JSONObject and add to arrayList here.
//                        final GuestHistory guestHistory = new GuestHistory();
//                        historyArrayList.add(guestHistory);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
        MessageHistoryAdapter historyAdapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
        ListView history = view.findViewById(R.id.lv_history_list);
        history.setAdapter(historyAdapter);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

}
