package com.example.dell.v_clock.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.LoginActivity;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.activity.SelectPhotoActivity;
import com.example.dell.v_clock.adapter.MessageHistoryAdapter;
import com.example.dell.v_clock.object.GuestHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This fragment shows the visitation history of one's guests.
 * 这个碎片展示了一个工作人员接待的嘉宾到访记录。
 */
public class HistoryFragment extends Fragment {

    View view;
    ArrayList<GuestHistory> historyArrayList;
    MessageHistoryAdapter adapter;
    RequestQueue requestQueue;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        historyArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());
        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, ServerInfo.DISPLAY_VISITING_RECORD_URL, null,
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
                                historyArrayList.add(guestHistory);
                                Log.d("History", guestHistory.getGuestName() + " " + guestHistory.getArriveTime());
                            }
                            adapter = new MessageHistoryAdapter(getContext(), R.layout.one_message_in_list, historyArrayList);
                            ListView historyList = view.findViewById(R.id.lv_history_list);
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
                eidMap.put("eid", "0004");
                return eidMap;
            }
        };
        requestQueue.add(jsonObjectRequest);
        Log.d("TAG", "onCreate in history fragment");
        return view ;
    }

    @Override
    public void onStop() {
        super.onStop();
        requestQueue.stop();
    }
    private class CustomRequest extends Request<JSONObject> {

        private Response.Listener<JSONObject> listener;
        private Map<String, String> params;

        public CustomRequest(String url, Map<String, String> params,
                             Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            this.listener = reponseListener;
            this.params = params;
        }

        public CustomRequest(int method, String url, Map<String, String> params,
                             Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = reponseListener;
            this.params = params;
        }

        protected Map<String, String> getParams()
                throws com.android.volley.AuthFailureError {
            return params;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse (NetworkResponse response) {
            try {
                String utf8String = new String(response.data, "UTF-8");
                return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                // log error
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                // log error
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(JSONObject response) {
            // TODO Auto-generated method stub
            listener.onResponse(response);
        }
    }

}

