package com.example.dell.v_clock.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.activity.AddGuestActivity;
import com.example.dell.v_clock.activity.GuestInfoActivity;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.adapter.GuestListAdapter;
import com.example.dell.v_clock.util.GuestListUtil;
import com.org.afinal.simplecache.ACache;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * This fragment shows the list of my guest and all guest.
 * 这个碎片展示我的嘉宾和所有嘉宾
 */
public class GuestListFragment extends Fragment implements View.OnClickListener,
        ExpandableListView.OnChildClickListener, SwipeRefreshLayout.OnRefreshListener {

    //嘉宾列表
    private ExpandableListView guestList;
    //添加嘉宾 按钮
    private ImageButton ibt_addGuest;
    //搜索按钮
    private Button bt_search;
    //嘉宾列表的适配器
    private GuestListAdapter guestListAdapter;
    //外侧列表的数据源
    private List<String> guestGroupList;

    //请求队列
    private RequestQueue requestQueue;
    String eid;

    //缓存对象
    private ACache mACache;
    //myGuestJson对象
    private JSONArray myGuestJsonArray = null;
    //allGuestJson对象
    private JSONArray allGuestJsonArray = null;

    private final int MY_GUEST_IDENTITOR = 0;
    private final int ALL_GUEST_IDENTITOR = 1;
    private final int FRESH_UI = 4;

    SwipeRefreshLayout swipeRefreshLayout;

    String TAG = "StartGuestList";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("guestList", "onCreate");
        View view = inflater.inflate(R.layout.fragment_guest_list, container, false);

        //初始化控件
        guestList = view.findViewById(R.id.explv_my_guest_list);
        ibt_addGuest = view.findViewById(R.id.img_bt_add);
        bt_search = view.findViewById(R.id.bt_search);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_guest_list);

        ibt_addGuest.setOnClickListener(this);
        bt_search.setOnClickListener(this);

        guestList.setOnChildClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        //初始化嘉宾列表
        initGuestList();

        return view;
    }

    /**
     * 初始化嘉宾列表
     */
    private void initGuestList() {
        //GroupList只包含两项
        guestGroupList = new ArrayList<>();
        guestGroupList.add(0, "我的嘉宾");
        guestGroupList.add(1, "其他嘉宾");
        //childList的信息来源于后台服务器
        //设置适配器
        guestListAdapter = new GuestListAdapter(this.getContext(), guestGroupList, GuestListUtil.guestChildList);
        guestList.setAdapter(guestListAdapter);
        //缓存对象
        mACache = ACache.get(getContext());
        //
        requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);
        //启动线程读取数据
        readCache();
    }

    /**
     * 启动线程读取缓存数据
     */
    private void readCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //加载ChildList数据
                if (GuestListUtil.getMyGuestList().size() > 0) {//内存中已有我的嘉宾数据
                    //数据更新 刷新UI
                    handler.sendEmptyMessage(FRESH_UI);
                } else {
                    Log.i(TAG, "内存中没有我的嘉宾数据,加载缓存");
                    //判断是否有缓存数据  没有请求后台
                    myGuestJsonArray = mACache.getAsJSONArray(GuestListUtil.MY_GUEST_JSON_ARRAY_CACHE);
                    //读取完我的嘉宾缓存后 判读是否 加载数据
                    cacheIsAvailable(myGuestJsonArray, MY_GUEST_IDENTITOR);
                }
                if (GuestListUtil.getAllGuestList().size() > 0) {
                    //数据更新 刷新UI
                    handler.sendEmptyMessage(FRESH_UI);
                } else {
                    Log.i(TAG, "内存中没有全部嘉宾数据,加载缓存");
                    //判断是否有缓存数据
                    allGuestJsonArray = mACache.getAsJSONArray(GuestListUtil.ALL_GUEST_JSON_ARRAY_CACHE);
                    //读取完全部嘉宾缓存后 判读是否 加载数据
                    cacheIsAvailable(allGuestJsonArray, ALL_GUEST_IDENTITOR);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //启动线程 若向数据库请求数据 检测是否完成
        refreshChildList();
    }

    /**
     * 接收Message 更改UI
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FRESH_UI:
                    //数据更新 刷新UI
                    guestListAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    /**
     * 读取完缓存后 判读是否 加载数据
     */
    private void cacheIsAvailable(final JSONArray guestJsonArray, final int identitor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (guestJsonArray == null || guestJsonArray.length() == 0) {
                    //请求服务器
                    if (identitor == 0) {
                        Log.i(TAG, "我的嘉宾缓存为空，请求服务器");
                        GuestListUtil.requestMyGuestList(requestQueue, eid, getContext());
                    } else if (identitor == 1) {
                        Log.i(TAG, "全部嘉宾缓存为空，请求服务器");
                        GuestListUtil.requestAllGuestList(requestQueue, eid, getContext());
                    }
                } else {
                    Log.i(TAG, "加载嘉宾缓存——" + identitor);
                    GuestListUtil.loadChildListData(guestJsonArray, identitor);
                }
            }
        }).start();
    }

    //下拉刷新 重新请求数据库
    @Override
    public void onRefresh() {
        GuestListUtil.requestMyGuestList(requestQueue, eid, getContext());
        GuestListUtil.requestAllGuestList(requestQueue, eid, getContext());
    }

    /**
     * 刷新ChildList的数据
     */
    private void refreshChildList() {
        //启动一个线程 检查数据是否更新完成
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        if (GuestListUtil.isMyFreshed()) {
                            handler.sendEmptyMessage(FRESH_UI);
                            GuestListUtil.setIsMyFreshed(false);
                        }
                        if (GuestListUtil.isAllFreshed()) {
                            handler.sendEmptyMessage(FRESH_UI);
                            GuestListUtil.setIsAllFreshed(false);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        guestList.expandGroup(MY_GUEST_IDENTITOR);
    }

    /**
     * 搜索按钮点击事件的监听
     *
     * @param view 点击的控件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                Log.i("Guest", "点击了搜索按钮");
                Intent search_intent = new Intent(getContext(), SearchActivity.class);
                startActivity(search_intent);
                break;
            case R.id.img_bt_add:
                Log.i("Guest", "点击了添加按钮");
                Intent add_intent = new Intent(getContext(), AddGuestActivity.class);
                startActivity(add_intent);
                break;
        }
    }

    /**
     * 列表项 点击事件的监听
     *
     * @param expandableListView The ExpandableListView where the click happened
     * @param view               The view within the expandable list/ListView that was clicked
     * @param groupPosition      The group position that contains the child thatwas clicked
     * @param childPosition      The child position within the group
     * @param id                 The row id of the child that was clicked
     * @return true if the click was handled
     */
    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view,
                                int groupPosition, int childPosition, long id) {
        //T判断点击的是哪一项
        String name = (String) GuestListUtil.guestChildList.get(groupPosition).get(childPosition).get("name");
        Intent guestInfoIntent = new Intent(getContext(), GuestInfoActivity.class);
        guestInfoIntent.putExtra("guest_type", groupPosition);
        guestInfoIntent.putExtra("gname", name);
        //todo  传照片
        startActivity(guestInfoIntent);
        return false;
    }
}


