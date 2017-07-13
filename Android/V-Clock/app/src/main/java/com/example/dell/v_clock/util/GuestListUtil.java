package com.example.dell.v_clock.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.ServerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 王庆伟 on 2017/7/13.
 */

public class GuestListUtil {
    //我的嘉宾 缓存 键名
    public static final String MY_GUEST_JSON_ARRAY_CACHE = "myGuestJsonArray";
    //全部嘉宾 缓存 键名
    public static final String ALL_GUEST_JSON_ARRAY_CACHE = "allGuestJsonArray";
    //缓存保存时间 单位 秒
    public static final int SAVE_TIME = 3600;


    //我的嘉宾 请求tip
    private static final String MY_GUEST_SEARCH_TYPE = "0";
    //全部嘉宾（gname="") 异步搜索 请求tip
    private static final String PARTIAL_NAME_SEARCH_TYPE = "1";
    //myGuestJson对象
    private static JSONArray myGuestJsonArray = null;
    //allGuestJson对象
    private static JSONArray allGuestJsonArray = null;


    public static void requestGuestList(Context context) {
        //重置数据
        myGuestJsonArray = null;
        allGuestJsonArray = null;

        //我的嘉宾 请求对象
        JSONObjectRequestMapParams myGuestRequest;
        //全部嘉宾 请求对象
        JSONObjectRequestMapParams allGuestRequest;
        //请求队列
        RequestQueue requestQueue;

        //向服务器发送请求  请求我的嘉宾
        Map<String, String> my_searchInfo = new HashMap<>();
        my_searchInfo.put("tip", MY_GUEST_SEARCH_TYPE);
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        String eid = sp.getString("eid", null);
        my_searchInfo.put("eid", eid);
        myGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, my_searchInfo,
                new MyGuestListResponseListener(), new GuestListResponseErrorListener());
        //  请求全部嘉宾
        Map<String, String> all_searchInfo = new HashMap<>();
        all_searchInfo.put("gname", "");
        all_searchInfo.put("tip", PARTIAL_NAME_SEARCH_TYPE);
        my_searchInfo.put("eid", eid);
        allGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, all_searchInfo,
                new AllGuestListResponseListener(), new GuestListResponseErrorListener());
        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(context);
        //发出请求
        requestQueue.add(myGuestRequest);
        requestQueue.add(allGuestRequest);
    }

    /**
     * @return myGuestJsonArray
     */
    public static JSONArray getMyGuestJsonArray() {
        return myGuestJsonArray;
    }

    /**
     * @return allGuestJsonArray
     */
    public static JSONArray getAllGuestJsonArray() {
        return allGuestJsonArray;
    }

    /**
     *
     */
    private static class MyGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("GuestList");
                myGuestJsonArray = jsonObjects;
            } catch (JSONException e) {
                //todo
//                Toast.makeText(context, "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private static class AllGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("Guest");
                allGuestJsonArray = jsonObjects;
            } catch (JSONException e) {
                //todo
//                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * 将JSONArray里的数据提取出来
     *
     * @param jsonObjects JasonArray对象
     * @return List对象
     */
    public static List<Map<String, Object>> jsonToList(JSONArray jsonObjects) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonObjects.length(); i++) {
                Map<String, Object> tempMap = new HashMap();
                String basePhoto = null;
                basePhoto = jsonObjects.getJSONObject(i).getString("gphoto");
                //手机号暂时不用
//              String phone = response.getString("gtel");
                tempMap.put("avatar", ImageUtil.convertImage(basePhoto));
                tempMap.put("name", jsonObjects.getJSONObject(i).getString("gname"));
                tempList.add(tempMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempList;
    }

    /**
     *
     */
    private static class GuestListResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败 todo
//            Toast.makeText(getContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
            //todo  隔一段时间再请求
//            refreshChildList();
        }
    }


}
