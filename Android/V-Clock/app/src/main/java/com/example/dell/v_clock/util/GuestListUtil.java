package com.example.dell.v_clock.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.object.GuestInfo;
import com.org.afinal.simplecache.ACache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 王庆伟 on 2017/7/13.
 *
 */

public class GuestListUtil {
    //我的嘉宾 缓存 键名
    public static final String MY_GUEST_JSON_ARRAY_CACHE = "myGuestJsonArray";
    //全部嘉宾 缓存 键名
    public static final String ALL_GUEST_JSON_ARRAY_CACHE = "allGuestJsonArray";
    //ChildList数据
    public static List<List<Map<String, Object>>> guestChildList;

    //我的嘉宾缓存保存时间 单位 秒
    private static final int MY_SAVE_TIME = 36000;
    //全部嘉宾缓存保存时间 单位 秒
    private static final int ALL_SAVE_TIME = 3600;
    //我的嘉宾 请求tip
    private static final String MY_GUEST_SEARCH_TYPE = "0";
    //全部嘉宾（gname="") 异步搜索 请求tip
    private static final String PARTIAL_NAME_SEARCH_TYPE = "1";
//    //myGuestJson对象
//    private static JSONArray myGuestJsonArray = null;
//    //allGuestJson对象
//    private static JSONArray allGuestJsonArray = null;

    private static final int MY_GUEST_IDENTITOR = 0;
    private static final int ALL_GUEST_IDENTITOR = 1;

    private static boolean isMyFreshed = false;
    private static boolean isAllFreshed = false;

    private static String TAG = "GuestListUtil";

    private static ACache mACache = null;

    static {
        guestChildList = new ArrayList<>();
        guestChildList.add(new ArrayList<Map<String, Object>>());
        guestChildList.add(new ArrayList<Map<String, Object>>());
    }

    /**
     * 向服务器请求我的嘉宾
     *
     * @param requestQueue requestQueue
     * @param eid          eid
     */
    public static void requestMyGuestList(RequestQueue requestQueue, String eid, Context context) {
        isMyFreshed = false;
        if (mACache == null) {
            mACache = ACache.get(context);
        }
        //向服务器发送请求  请求我的嘉宾
        Map<String, String> my_searchInfo = new HashMap<>();
        my_searchInfo.put("tip", MY_GUEST_SEARCH_TYPE);
        my_searchInfo.put("eid", eid);
        JSONObjectRequestMapParams myGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, my_searchInfo,
                new MyGuestListResponseListener(), new GuestListResponseErrorListener());
        //发出请求
        Log.i(TAG, "向服务器发出 我的嘉宾 请求");
        requestQueue.add(myGuestRequest);
    }

    /**
     * @param requestQueue requestQueue
     */
    public static void requestAllGuestList(RequestQueue requestQueue, String eid, Context context) {
        isAllFreshed = false;
        if (mACache == null) {
            mACache = ACache.get(context);
        }
        //  请求全部嘉宾
        Map<String, String> all_searchInfo = new HashMap<>();
        all_searchInfo.put("gname", "");
        all_searchInfo.put("eid", eid);
        all_searchInfo.put("tip", PARTIAL_NAME_SEARCH_TYPE);
        JSONObjectRequestMapParams allGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, all_searchInfo,
                new AllGuestListResponseListener(), new GuestListResponseErrorListener());
        //发出请求
        Log.i(TAG, "向服务器发出 全部嘉宾 请求");
        requestQueue.add(allGuestRequest);
    }

    /**
     * 新增嘉宾
     *
     * @param guest 嘉宾信息
     */
    public static void addGuest(GuestInfo guest, Context context) {
        //更新内存数据
        addToList(guest, MY_GUEST_IDENTITOR);
        isMyFreshed = true;

        //移除本地缓存
        removeCache(MY_GUEST_IDENTITOR, context);
    }

    /**
     * @param guest   guest
     * @param context context
     */
    public static void addToMyGuest(GuestInfo guest, Context context) {
        //更新内存数据
        //添加至我的嘉宾
        addToList(guest, MY_GUEST_IDENTITOR);
        //从其他嘉宾中删除
        removeFromList(guest, ALL_GUEST_IDENTITOR);
        isMyFreshed = true;
        isAllFreshed = true;
        //移除本地缓存
        removeCache(MY_GUEST_IDENTITOR, context);
        removeCache(ALL_GUEST_IDENTITOR, context);
    }

    /**
     * @param guest     guest
     * @param identitor myGuestIdentitor
     */
    private static void addToList(GuestInfo guest, int identitor) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", guest.getGuestName());
        temp.put("avatar", guest.getGuestBitmapPhoto());
        guestChildList.get(identitor).add(temp);
    }

    /**
     * 从内存中删除 list中的某项
     *
     * @param guestInfo guestInfo
     * @param identitor identitor
     */
    private static void removeFromList(GuestInfo guestInfo, int identitor) {
        int index;
        for (Map<String, Object> temp : guestChildList.get(identitor)) {
            if (temp.get("name").equals(guestInfo.getGuestName())) {
                index = guestChildList.get(identitor).indexOf(temp);
                guestChildList.get(identitor).remove(index);
                break;
            }
        }
    }

    /**
     * @param identitor identitor
     * @param context   context
     */
    private static void removeCache(int identitor, Context context) {
        ACache mACache = ACache.get(context);
        if (identitor == MY_GUEST_IDENTITOR) {
            mACache.remove(MY_GUEST_JSON_ARRAY_CACHE);
        } else if (identitor == ALL_GUEST_IDENTITOR) {
            mACache.remove(ALL_GUEST_JSON_ARRAY_CACHE);
        }
    }

    /**
     * 从我的嘉宾中移除
     *
     * @param guest 嘉宾信息
     */
    public static void deleteFromMyGuest(GuestInfo guest, Context context) {
        //更新内存数据
        removeFromList(guest, MY_GUEST_IDENTITOR);
        //添加至其他嘉宾
        addToList(guest, ALL_GUEST_IDENTITOR);
        isMyFreshed = true;
        isAllFreshed = true;
        //删除缓存
        removeCache(MY_GUEST_IDENTITOR, context);
        removeCache(ALL_GUEST_IDENTITOR, context);
    }

    /**
     * 修改照片
     *
     * @param guest 嘉宾信息
     */
    public static void modifyPhoto(GuestInfo guest, Context context) {
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
            removeCache(MY_GUEST_IDENTITOR, context);
            removeCache(ALL_GUEST_IDENTITOR, context);
        } else {
            removeCache(ALL_GUEST_IDENTITOR, context);
        }
        isMyFreshed = true;

        removeCache(guest.getGuest_type(), context);
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
                Log.i(TAG, "收到服务器 我的嘉宾 回复 JsonArray.length = " + jsonObjects.length());
//                setValueToJason(jsonObjects, myGuestJsonArray);
                loadChildListData(jsonObjects, MY_GUEST_IDENTITOR);
                //写缓存
                refreshCache(jsonObjects, MY_GUEST_IDENTITOR);
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
                Log.i(TAG, "收到服务器 全部嘉宾 回复 JsonArray.length = " + jsonObjects.length());
//                setValueToJason(jsonObjects, allGuestJsonArray);
                loadChildListData(jsonObjects, ALL_GUEST_IDENTITOR);
                //写缓存
                refreshCache(jsonObjects, ALL_GUEST_IDENTITOR);
            } catch (JSONException e) {
                Log.i(TAG, "收到服务器 全部嘉宾 数据错误回复");
                //todo
//                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * 写缓存
     *
     * @param jsonObjects    jsonObjects
     * @param guestIdentitor guestIdentitor
     */
    private static void refreshCache(final JSONArray jsonObjects, final int guestIdentitor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (guestIdentitor == MY_GUEST_IDENTITOR) {
                    mACache.put(MY_GUEST_JSON_ARRAY_CACHE, jsonObjects, MY_SAVE_TIME);
                } else if (guestIdentitor == ALL_GUEST_IDENTITOR) {
                    mACache.put(ALL_GUEST_JSON_ARRAY_CACHE, jsonObjects, ALL_SAVE_TIME);
                }

            }
        }).start();
    }

    /**
     * 加载ChildList信息
     *
     * @param guestJsonArray 嘉宾信息
     * @param i              我的嘉宾：0 ； 全部嘉宾：1
     */
    public static void loadChildListData(JSONArray guestJsonArray, int i) {
        List<Map<String, Object>> temp = new ArrayList<>();
        for (Map<String, Object> t : guestChildList.get(ALL_GUEST_IDENTITOR)) {
            temp.add(t);
        }

        if (guestJsonArray != null) {
            if (guestChildList.size() > i) {
                guestChildList.get(i).clear();
            }
            List<Map<String, Object>> tempList = jsonToList(guestJsonArray);
            guestChildList.add(i, tempList);
            if (i == MY_GUEST_IDENTITOR) {
                guestChildList.add(ALL_GUEST_IDENTITOR, temp);
//                Log.i(TAG, "我的嘉宾 数据转换完成 guestChildList.get(0).size() = " + guestChildList.get(i).size());
                isMyFreshed = true;
            } else if (i == ALL_GUEST_IDENTITOR) {
//                Log.i(TAG, "全部嘉宾 数据转换完成 guestChildList.get(1).size() = " + guestChildList.get(i).size());
                isAllFreshed = true;
            }
        }
    }

    /**
     * 清空内存数据
     */
    public static void clearList() {
        guestChildList.clear();
        guestChildList = null;
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
                String basePhoto = jsonObjects.getJSONObject(i).getString("gphoto");
                //手机号暂时不用
//              String phone = response.getString("gtel");
                tempMap.put("avatar", ImageUtil.convertImage(basePhoto));
                tempMap.put("name", jsonObjects.getJSONObject(i).getString("gname"));
//                Log.i(TAG,"name = "+jsonObjects.getJSONObject(i).getString("gname"));
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
     * @return 全部嘉宾列表数据
     */
    public static List<Map<String, Object>> getAllGuestList() {
        if (guestChildList.size() < ALL_GUEST_IDENTITOR) {
            guestChildList.add(ALL_GUEST_IDENTITOR, new ArrayList<Map<String, Object>>());
        }
        return guestChildList.get(ALL_GUEST_IDENTITOR);
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                //当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    //当前所连接的网络可用
                    return true;
                }
            }
        }

        return false;
    }

}
