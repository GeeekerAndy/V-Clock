package com.example.dell.v_clock.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.object.GuestInfo;

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
    //我的嘉宾缓存保存时间 单位 秒
    public static final int MY_SAVE_TIME = 3600;
    //全部嘉宾缓存保存时间 单位 秒
    public static final int ALL_SAVE_TIME = 1800;
    //广播相关
//    public static final String BROADCAST_ACTION = "jason.broadcast.action";
//    public static final String BROADCAST_KEY = "receiver";
//    public static final String BROADCAST_VALUE = "GuestListFragment";
//    public static final String MY_GUEST = "myGuest";
//    public static final String ALL_GUEST = "allGuest";

    //我的嘉宾 请求tip
    private static final String MY_GUEST_SEARCH_TYPE = "0";
    //全部嘉宾（gname="") 异步搜索 请求tip
    private static final String PARTIAL_NAME_SEARCH_TYPE = "1";
    //myGuestJson对象
    private static JSONArray myGuestJsonArray = null;
    //allGuestJson对象
    private static JSONArray allGuestJsonArray = null;
    //ChildList数据
    private static List<List<Map<String, Object>>> guestChildList = new ArrayList<>();

    private static final int MY_GUEST_IDENTITOR = 0;
    private static final int ALL_GUEST_IDENTITOR = 1;

    private static boolean isMyFreshed = false;
    private static boolean isAllFreshed = false;

    //我的嘉宾 请求对象
    private static JSONObjectRequestMapParams myGuestRequest;
    //全部嘉宾 请求对象
    private static JSONObjectRequestMapParams allGuestRequest;
    //请求队列
    private static RequestQueue requestQueue;

    String TAG = "GuestListUtil";

    static {
        guestChildList.add(new ArrayList<Map<String, Object>>());
        guestChildList.add(new ArrayList<Map<String, Object>>());
    }

    public static void requestMyGuestList(Context context) {
        isMyFreshed = false;
        //向服务器发送请求  请求我的嘉宾
        Map<String, String> my_searchInfo = new HashMap<>();
        my_searchInfo.put("tip", MY_GUEST_SEARCH_TYPE);
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        String eid = sp.getString("eid", null);
        my_searchInfo.put("eid", eid);
        myGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, my_searchInfo,
                new MyGuestListResponseListener(), new GuestListResponseErrorListener());
        requestQueue = Volley.newRequestQueue(context);
        //发出请求
        requestQueue.add(myGuestRequest);
    }

    public static void requestAllGuestList(Context context) {
        isAllFreshed = false;
        //  请求全部嘉宾
        Map<String, String> all_searchInfo = new HashMap<>();
        all_searchInfo.put("gname", "");
        all_searchInfo.put("tip", PARTIAL_NAME_SEARCH_TYPE);
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        String eid = sp.getString("eid", null);
        all_searchInfo.put("eid", eid);
        allGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, all_searchInfo,
                new AllGuestListResponseListener(), new GuestListResponseErrorListener());
        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(context);
        //发出请求
        requestQueue.add(allGuestRequest);
    }

    /**
     * 新增嘉宾
     *
     * @param guest 嘉宾信息
     */
    public static void addGuest(GuestInfo guest) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", guest.getGuestName());
        temp.put("avatar", guest.getGuestBitmapPhoto());
        guestChildList.get(ALL_GUEST_IDENTITOR).add(temp);
        isAllFreshed = true;

        //todo  更新本地缓存
    }

    /**
     * 已有嘉宾添加至我的嘉宾
     *
     * @param guest 嘉宾信息
     */
    public static void addToMyGuest(GuestInfo guest) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", guest.getGuestName());
        temp.put("avatar", guest.getGuestBitmapPhoto());
        guestChildList.get(MY_GUEST_IDENTITOR).add(temp);
        isMyFreshed = true;

        //todo 更新本地缓存
    }

    /**
     * 从我的嘉宾中移除
     *
     * @param guest 嘉宾信息
     */
    public static void deleteFromMyGuest(GuestInfo guest) {
        int index = 0;
        for (Map<String, Object> temp : guestChildList.get(MY_GUEST_IDENTITOR)) {
            if (temp.get("name").equals(guest.getGuestName())) {
                index = guestChildList.get(MY_GUEST_IDENTITOR).indexOf(temp);
                guestChildList.get(MY_GUEST_IDENTITOR).remove(index);
                break;
            }
        }
        isMyFreshed = true;

        //todo 更新本地缓存
    }







    /**
     * 修改照片
     *
     * @param guest
     */
    public static void modifyPhoto(GuestInfo guest) {
        for (Map<String, Object> temp : guestChildList.get(guest.getGuest_type())) {
            if (temp.get("name").equals(guest.getGuestName())) {
                temp.put("avatar", guest.getGuestBitmapPhoto());
                break;
            }
        }
        if (guest.getGuest_type() == MY_GUEST_IDENTITOR) {
            for (Map<String, Object> temp : guestChildList.get(ALL_GUEST_IDENTITOR)) {
                if (temp.get("name").equals(guest.getGuestName())) {
                    temp.put("avatar", guest.getGuestBitmapPhoto());
                    break;
                }
            }
        }
        isMyFreshed = true;
    }


    /**
     * 复制一个List 到 另一个List
     *
     * @param sourceList 源List
     * @param toList     目的List
     */
    public static void setValueToList(List<Map<String, Object>> sourceList, List<Map<String, Object>> toList) {
        for (Map<String, Object> temp : sourceList) {
            toList.add(temp);
        }
    }


    /**
     * 获取 我的嘉宾 信息的 后台回复监听器
     */
    private static class MyGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("GuestList");
                myGuestJsonArray = jsonObjects;
                loadChildListData(myGuestJsonArray, MY_GUEST_IDENTITOR);
            } catch (JSONException e) {
                //todo
//                Toast.makeText(context, "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取 全部嘉宾 信息的 后台回复监听器
     */
    private static class AllGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("Guest");
                allGuestJsonArray = jsonObjects;
                loadChildListData(allGuestJsonArray, ALL_GUEST_IDENTITOR);
            } catch (JSONException e) {
                //todo
//                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    /**
     * 加载ChildList信息
     *
     * @param guestJsonArray 嘉宾信息
     * @param i              我的嘉宾：0 ； 全部嘉宾：1
     */
    private static void loadChildListData(JSONArray guestJsonArray, int i) {
        if (guestJsonArray != null) {
            if (guestChildList.size() > i) {
                guestChildList.get(i).clear();
            }
            List<Map<String, Object>> tempList = jsonToList(guestJsonArray);
            guestChildList.add(i, tempList);
            if (i == MY_GUEST_IDENTITOR) {
                isMyFreshed = true;
            } else if (i == ALL_GUEST_IDENTITOR) {
                isAllFreshed = false;
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
     * @return isMyFreshed
     */
    public static boolean isMyFreshed() {
        return isMyFreshed;
    }

    /**
     * @return isAllFreshed
     */
    public static boolean isAllFreshed() {
        return isAllFreshed;
    }

    /**
     * @return 我的嘉宾列表数据
     */
    public static List<Map<String, Object>> getMyGuestList() {
        if (guestChildList.size() < MY_GUEST_IDENTITOR) {
            guestChildList.add(MY_GUEST_IDENTITOR, new ArrayList<Map<String, Object>>());
        }
        return guestChildList.get(MY_GUEST_IDENTITOR);
    }

    /**
     * @param myGuestList myGuestList
     */
    public static void setMyGuestList(List<Map<String, Object>> myGuestList) {
        if (guestChildList.size() < MY_GUEST_IDENTITOR) {
            guestChildList.add(MY_GUEST_IDENTITOR, new ArrayList<Map<String, Object>>());
        }
        setValueToList(myGuestList, guestChildList.get(MY_GUEST_IDENTITOR));
//        for (Map<String, Object> temp : myGuestList) {
//            guestChildList.get(MY_GUEST_IDENTITOR).add(temp);
//        }
    }

    /**
     * @return 全部嘉宾列表数据
     */
    public static List<Map<String, Object>> getAllGuestList() {
        if (guestChildList.size() < ALL_GUEST_IDENTITOR) {
            guestChildList.add(ALL_GUEST_IDENTITOR, new ArrayList<Map<String, Object>>());
        }
        return guestChildList.get(ALL_GUEST_IDENTITOR);
    }

    /**
     * @param allGuestList
     */
    public static void setAllGuestList(List<Map<String, Object>> allGuestList) {
        if (guestChildList.size() < ALL_GUEST_IDENTITOR) {
            guestChildList.add(ALL_GUEST_IDENTITOR, new ArrayList<Map<String, Object>>());
        }
        setValueToList(allGuestList, guestChildList.get(ALL_GUEST_IDENTITOR));
//        for (Map<String, Object> temp : allGuestList) {
//            guestChildList.get(ALL_GUEST_IDENTITOR).add(temp);
//        }
    }

    /**
     * @param isMyFreshed isMyFreshed
     */
    public static void setIsMyFreshed(boolean isMyFreshed) {
        GuestListUtil.isMyFreshed = isMyFreshed;
    }

    /**
     * @param isAllFreshed isAllFreshed
     */
    public static void setIsAllFreshed(boolean isAllFreshed) {
        GuestListUtil.isAllFreshed = isAllFreshed;
    }

}
