package com.example.dell.v_clock.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.fragment.GuestListFragment;
import com.example.dell.v_clock.object.GuestInfo;
import com.org.afinal.simplecache.ACache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 王庆伟 on 2017/7/13.
 */

public class GuestListUtil {
    //我的嘉宾姓名 缓存 键名
    public static final String MY_GUEST_NAME_CACHE = "myGuestJsonArray";
    //全部嘉宾姓名 缓存 键名
    public static final String ALL_GUEST_NAME_CACHE = "allGuestJsonArray";
    //ChildList数据
    public static List<List<Map<String, Object>>> guestChildList;
    //
    public static final int LOAD_AVATAR_NUM = 20;
    //
    public static String AVATAR_CACHE = "bitmap_avatar";

    private static final int MY_MAX_THREAD_NUM = 5;
    private static final int ALL_MAX_THREAD_NUM = 10;

    private static ArrayList<String> myGuestNameList;
    private static ArrayList<String> allGuestNameList;

    //我的嘉宾缓存保存时间 单位 秒  默认一天
    private static final int MY_SAVE_TIME = 86400;
    //全部嘉宾缓存保存时间 单位 秒  默认一天
    private static final int ALL_SAVE_TIME = 86400;
    //我的嘉宾 请求tip
    private static final String MY_GUEST_SEARCH_TYPE = "0";
    //全部嘉宾（gname="") 异步搜索 请求tip
    private static final String PARTIAL_NAME_SEARCH_TYPE = "1";

    private static final int MY_GUEST_IDENTITOR = 0;
    private static final int ALL_GUEST_IDENTITOR = 1;


    private static ExecutorService myExecutorService;
    private static ExecutorService allExecutorService;

    //我的嘉宾是否刷新完成
    private static boolean isMyFreshed = false;
    //其他嘉宾是否刷新完成
    private static boolean isAllFreshed = false;
    //我的嘉宾是否可以刷新
    private static boolean isMYFreshedable = true;
    //其他嘉宾是否可以刷新
    private static boolean isAllFreshedable = true;
    //我的嘉宾是否可以刷新
    private static boolean isMYNameLoad = false;
    //其他嘉宾是否可以刷新
    private static boolean isAllNameLoad = false;
    //
    private static String TAG = "GuestListUtil";
    //缓存对象
    private static ACache mACache = null;

    static {
        guestChildList = new ArrayList<>();
        guestChildList.add(new ArrayList<Map<String, Object>>());
        guestChildList.add(new ArrayList<Map<String, Object>>());
        myExecutorService = Executors.newFixedThreadPool(MY_MAX_THREAD_NUM);
        allExecutorService = Executors.newFixedThreadPool(ALL_MAX_THREAD_NUM);
        myGuestNameList = new ArrayList<>();
        allGuestNameList = new ArrayList<>();
    }

    /**
     * 向服务器请求我的嘉宾
     *
     * @param requestQueue requestQueue
     * @param eid          eid
     */
    public static void requestMyGuestList(RequestQueue requestQueue, String eid, Context context) {
        myExecutorService = Executors.newFixedThreadPool(MY_MAX_THREAD_NUM);
        myGuestNameList = new ArrayList<>();
        isMyFreshed = false;
        isMYFreshedable = false;
        if (mACache == null) {
            mACache = ACache.get(context);
        }
        //向服务器发送请求  请求我的嘉宾
        Map<String, String> my_searchInfo = new HashMap<>();
        my_searchInfo.put("tip", MY_GUEST_SEARCH_TYPE);
        my_searchInfo.put("eid", eid);
        JSONObjectRequestMapParams myGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, my_searchInfo,
                new MyGuestListResponseListener(), new MyGuestListResponseErrorListener());
        //发出请求
        Log.i(TAG, "向服务器发出 我的嘉宾 请求");
        requestQueue.add(myGuestRequest);
    }

    /**
     * 向服务器请求其他嘉宾
     *
     * @param requestQueue requestQueue
     */
    public static void requestAllGuestList(RequestQueue requestQueue, String eid, Context context) {
        allExecutorService = Executors.newFixedThreadPool(ALL_MAX_THREAD_NUM);
        allGuestNameList = new ArrayList<>();
        isAllFreshed = false;
        isAllFreshedable = false;
        if (mACache == null) {
            mACache = ACache.get(context);
        }
        //  请求全部嘉宾
        Map<String, String> all_searchInfo = new HashMap<>();
        all_searchInfo.put("gname", "");
        all_searchInfo.put("eid", eid);
        all_searchInfo.put("tip", PARTIAL_NAME_SEARCH_TYPE);
        JSONObjectRequestMapParams allGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, all_searchInfo,
                new AllGuestListResponseListener(), new AllGuestListResponseErrorListener());
        //发出请求
        Log.i(TAG, "向服务器发出 全部嘉宾 请求");
        requestQueue.add(allGuestRequest);
    }

    /**
     * 新增嘉宾
     *
     * @param guest 嘉宾信息
     */
    public static void addGuest(GuestInfo guest) {
        //更新内存数据 及缓存数据
        addToList(guest, MY_GUEST_IDENTITOR);
        isMyFreshed = true;
    }

    /**
     * 添加到我的嘉宾
     *
     * @param guest   guest
     * @param context context
     */
    public static void addToMyGuest(GuestInfo guest, Context context) {
        //更新内存数据 及缓存数据
        //添加至我的嘉宾
        addToList(guest, MY_GUEST_IDENTITOR);
        //从其他嘉宾中删除
        removeFromList(guest, ALL_GUEST_IDENTITOR);
        isMyFreshed = true;
        isAllFreshed = true;
    }

    /**
     * 向内存及缓存 指定的列表中 添加嘉宾
     *
     * @param guest     guest
     * @param identitor myGuestIdentitor
     */
    private static void addToList(GuestInfo guest, int identitor) {
        //更新内存
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", guest.getGuestName());
        temp.put("avatar", guest.getGuestBitmapPhoto());
        guestChildList.get(identitor).add(temp);
        //更新缓存
        if (identitor == MY_GUEST_IDENTITOR) {
            myGuestNameList.add(guest.getGuestName());
            mACache.put(MY_GUEST_NAME_CACHE, myGuestNameList);
        } else if (identitor == ALL_GUEST_IDENTITOR) {
            allGuestNameList.add(guest.getGuestName());
            mACache.put(ALL_GUEST_NAME_CACHE, allGuestNameList);
        }
    }

    /**
     * 从内存中删除 list中的某项
     *
     * @param guestInfo guestInfo
     * @param identitor identitor
     */
    private static void removeFromList(GuestInfo guestInfo, int identitor) {
        //更新内存
        for (Map<String, Object> temp : guestChildList.get(identitor)) {
            if (temp.get("name").equals(guestInfo.getGuestName())) {
                guestChildList.get(identitor).remove(temp);
                break;
            }
        }
        //更新缓存
        if (identitor == MY_GUEST_IDENTITOR) {
            for (String name : myGuestNameList) {
                if (name.equals(guestInfo.getGuestName())) {
                    myGuestNameList.remove(name);
                    mACache.put(MY_GUEST_NAME_CACHE, myGuestNameList);
                    break;
                }
            }
        } else if (identitor == ALL_GUEST_IDENTITOR) {
            for (String name : allGuestNameList) {
                if (name.equals(guestInfo.getGuestName())) {
                    allGuestNameList.remove(name);
                    mACache.put(ALL_GUEST_NAME_CACHE, allGuestNameList);
                    break;
                }
            }
        }
    }

//    /**
//     * @param identitor identitor
//     * @param context   context
//     */
//    private static void removeCache(int identitor, Context context) {
//        ACache mACache = ACache.get(context);
//        if (identitor == MY_GUEST_IDENTITOR) {
//            mACache.remove(MY_GUEST_NAME_CACHE);
//        } else if (identitor == ALL_GUEST_IDENTITOR) {
//            mACache.remove(ALL_GUEST_NAME_CACHE);
//        }
//    }

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
                mACache.put(guest.getGuestName() + AVATAR_CACHE, guest.getGuestBitmapPhoto());
                break;
            }
        }
        isMyFreshed = true;
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
                loadChildListData(jsonObjects, MY_GUEST_IDENTITOR);
            } catch (JSONException e) {
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
                loadChildListData(jsonObjects, ALL_GUEST_IDENTITOR);
            } catch (JSONException e) {
                Log.i(TAG, "收到服务器 全部嘉宾 数据错误回复");
//                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载ChildList信息
     *
     * @param guestJsonArray 嘉宾信息
     * @param identitor      我的嘉宾：0 ； 全部嘉宾：1
     */
    private static void loadChildListData(final JSONArray guestJsonArray, final int identitor) throws JSONException {
        //解决guestChildList.(0).clear时，guestChildList.(1)也会清空的问题
        List<Map<String, Object>> temp = new ArrayList<>();
        for (Map<String, Object> t : guestChildList.get(ALL_GUEST_IDENTITOR)) {
            temp.add(t);
        }

        if (guestJsonArray != null) {
            if (guestChildList.size() > identitor) {
                guestChildList.get(identitor).clear();
            }
            //将JSonArray转为ArrayList
            List<Map<String, Object>> tempList = jsonToList(guestJsonArray);
            guestChildList.add(identitor, tempList);
            if (identitor == MY_GUEST_IDENTITOR) {
                guestChildList.add(ALL_GUEST_IDENTITOR, temp);
                Log.i(TAG, "我的嘉宾 数据转换完成 guestChildList.get(0).size() = " + guestChildList.get(identitor).size());
                isMyFreshed = true;
                isMYFreshedable = true;
                //将所有我的嘉宾 姓名 加入姓名列表  并写入缓存
                putNameCache(identitor);
            } else if (identitor == ALL_GUEST_IDENTITOR) {
                Log.i(TAG, "全部嘉宾 数据转换完成 guestChildList.get(1).size() = " + guestChildList.get(identitor).size());
                isAllFreshed = true;
                isAllFreshedable = true;
                //将所有全部嘉宾 姓名 加入姓名列表  并写入缓存
                putNameCache(identitor);
            }
            for (int i = 0; i < guestJsonArray.length(); i++) {
                String name = guestJsonArray.getJSONObject(i).getString("gname");
                String basePhoto = guestJsonArray.getJSONObject(i).getString("gphoto");
                Bitmap avatar = ImageUtil.convertImage(basePhoto);
                //缓存图片
                CacheAllAvatar(name, avatar, identitor);
            }
            ExecutorService[] threadPools = {myExecutorService, allExecutorService};
            threadPools[identitor].shutdown();
        }
    }

    /**
     * 缓存所有图片
     *
     * @param name   用户姓名
     * @param avatar 用户头像
     */
    private static void CacheAllAvatar(final String name, final Bitmap avatar, final int identitor) {
        final ExecutorService[] threadPools = {myExecutorService, allExecutorService};
        threadPools[identitor].execute(new Runnable() {
            @Override
            public void run() {
                mACache.put(name + AVATAR_CACHE, avatar, MY_SAVE_TIME);
//                Log.i(TAG, name + "   的头像缓存完成");
            }
        });
    }

    /**
     * 加载缓存读取的 姓名列表 到 用户列表中
     *
     * @param nameList  姓名列表
     * @param identitor 用户类型标识
     */
    public static void loadChildListDataFromCache(ArrayList<String> nameList, final int identitor, final Context context) {
        if (guestChildList.size() > identitor) {
            guestChildList.get(identitor).clear();
        }
        for (int i = 0; i < nameList.size(); i++) {
            Map<String, Object> temp = new HashMap<>();
            String name = nameList.get(i);
            temp.put("name", name);
            Bitmap avatar = null;
            if (i < 10) {
                //保证mACache初始化
                if (mACache == null) {
                    mACache = ACache.get(context);
                }
                avatar = mACache.getAsBitmap(name + AVATAR_CACHE);
            }
            temp.put("avatar", avatar);
            guestChildList.get(identitor).add(temp);
        }
        //读缓存图片
        for (int i = 0; i < guestChildList.size(); i++) {
            if (i >= LOAD_AVATAR_NUM) {
                break;
            }
            Map<String, Object> tempMap = guestChildList.get(identitor).get(i);
            tempMap.put("avatar", mACache.getAsBitmap((String) tempMap.get("name")));
        }
        if (identitor == MY_GUEST_IDENTITOR) {
            isMyFreshed = true;
        } else if (identitor == ALL_GUEST_IDENTITOR) {
            isAllFreshed = true;
        }
        //如果数据不为空 则清空
        if (isMYNameLoad) {
            myGuestNameList.clear();
            isMYNameLoad = false;
        }
        if (isAllNameLoad) {
            allGuestNameList.clear();
            isAllNameLoad = false;
        }
        //复制姓名列表
        if (identitor == MY_GUEST_IDENTITOR) {
            for (String name : nameList) {
                myGuestNameList.add(name);
            }
            isMYNameLoad = true;
        } else if (identitor == ALL_GUEST_IDENTITOR) {
            for (String name : nameList) {
                allGuestNameList.add(name);
            }
            isAllNameLoad = true;
        }


    }


    /**
     * 将所有嘉宾 姓名 加入姓名列表  并写入缓存
     *
     * @param identitor 嘉宾类型标识
     */
    private static void putNameCache(final int identitor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (identitor == MY_GUEST_IDENTITOR) {
                    if (myGuestNameList.size() > 0) {
                        myGuestNameList.clear();
                    }
                    for (Map<String, Object> temp : guestChildList.get(identitor)) {
                        myGuestNameList.add((String) temp.get("name"));
                    }
                    synchronized (SearchActivity.class) {
                        mACache.put(MY_GUEST_NAME_CACHE, myGuestNameList, MY_SAVE_TIME);
                    }
                } else if (identitor == ALL_GUEST_IDENTITOR) {
                    if (allGuestNameList.size() > 0) {
                        allGuestNameList.clear();
                    }
                    for (Map<String, Object> temp : guestChildList.get(identitor)) {
                        allGuestNameList.add((String) temp.get("name"));
                    }
                    synchronized (SearchActivity.class) {
                        mACache.put(ALL_GUEST_NAME_CACHE, allGuestNameList, MY_SAVE_TIME);
                    }

                }

            }
        }).start();
    }

    /**
     * 清空内存数据
     */
    public static void clearList() {
        guestChildList.clear();
        guestChildList = null;
        myGuestNameList.clear();
        myGuestNameList = null;
        allGuestNameList.clear();
        allGuestNameList = null;
    }

    /**
     * 将JSONArray里的数据提取出来
     *
     * @param jsonObjects JasonArray对象
     * @return List对象
     */
    public static List<Map<String, Object>> jsonToList(JSONArray jsonObjects) {
        final List<Map<String, Object>> tempList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonObjects.length(); i++) {
                Map<String, Object> tempMap = new HashMap();
                String name = jsonObjects.getJSONObject(i).getString("gname");
                String basePhoto = jsonObjects.getJSONObject(i).getString("gphoto");
                Bitmap avatar = ImageUtil.convertImage(basePhoto);
                tempMap.put("name", name);
                tempMap.put("avatar", avatar);
                if (i < LOAD_AVATAR_NUM) {
                    tempMap.put("avatar", avatar);
                } else {
                    tempMap.put("avatar", null);
                }
                tempList.add(tempMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempList;
    }

    /**
     *
     */
    private static class MyGuestListResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            isMYFreshedable = true;
            Log.i("Transfer", "收到服务器回复");
        }
    }

    private static class AllGuestListResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            isAllFreshedable = true;
            Log.i("Transfer", "收到服务器回复");
        }
    }

    public static boolean isMYFreshedable() {
        return isMYFreshedable;
    }

    public static boolean isAllFreshedable() {
        return isAllFreshedable;
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
