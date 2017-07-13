package com.example.dell.v_clock.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.AddGuestActivity;
import com.example.dell.v_clock.activity.GuestInfoActivity;
import com.example.dell.v_clock.activity.MainActivity;
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.adapter.GuestListAdapter;
import com.example.dell.v_clock.util.GuestListUtil;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;
import com.org.afinal.simplecache.ACache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * This fragment shows the list of my guest and all guest.
 * 这个碎片展示我的嘉宾和所有嘉宾
 */
public class GuestListFragment extends Fragment implements View.OnClickListener,
        ExpandableListView.OnChildClickListener {

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
    //内层列表的数据源
    private List<List<Map<String, Object>>> guestChildList;

    //缓存对象
    private ACache mACache;
    //myGuestJson对象
    private JSONArray myGuestJsonArray = null;
    //allGuestJson对象
    private JSONArray allGuestJsonArray = null;

    //是否可加载  请求服务器时 不可加载
    private boolean isLoadable = false;


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

        ibt_addGuest.setOnClickListener(this);
        bt_search.setOnClickListener(this);

        guestList.setOnChildClickListener(this);

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
        guestGroupList.add(1, "全部嘉宾");
        //childList的信息来源于后台服务器
        guestChildList = new ArrayList<>();
        //设置适配器
        guestListAdapter = new GuestListAdapter(this.getContext(), guestGroupList, guestChildList);
        guestList.setAdapter(guestListAdapter);

        //加载ChildList数据
        //判断是否有缓存数据
        mACache = ACache.get(getContext());
        myGuestJsonArray = mACache.getAsJSONArray(GuestListUtil.MY_GUEST_JSON_ARRAY_CACHE);
        allGuestJsonArray = mACache.getAsJSONArray(GuestListUtil.ALL_GUEST_JSON_ARRAY_CACHE);
        if (myGuestJsonArray == null || allGuestJsonArray == null) {
            isLoadable = false;
            //请求服务器
            GuestListUtil.requestGuestList(getContext());
        } else {
            isLoadable = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoadable) {
            loadChildListData(myGuestJsonArray, 0);
            loadChildListData(allGuestJsonArray, 1);
        }
        refreshChildList();
    }

    /**
     * 加载ChildList信息
     *
     * @param guestJsonArray 嘉宾信息
     * @param i              我的嘉宾：0； 全部嘉宾：1
     */
    private void loadChildListData(JSONArray guestJsonArray, int i) {
        if (guestJsonArray != null) {
            if (guestChildList.size() > i) {
                guestChildList.get(i).clear();
            }
            List<Map<String, Object>> tempList = GuestListUtil.jsonToList(guestJsonArray);
            guestChildList.add(i, tempList);
        }
    }

    //TODO 下拉刷新  isLoadable = false

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
                        myGuestJsonArray = GuestListUtil.getMyGuestJsonArray();
                        allGuestJsonArray = GuestListUtil.getAllGuestJsonArray();
                        if (myGuestJsonArray != null && !isLoadable) {
                            //更新我的嘉宾列表
                            loadChildListData(myGuestJsonArray, 0);
                            //存储缓存
                            mACache.put(GuestListUtil.MY_GUEST_JSON_ARRAY_CACHE,
                                    myGuestJsonArray, GuestListUtil.SAVE_TIME);
                        }
                        if (allGuestJsonArray != null  && !isLoadable) {
                            //更新全部嘉宾列表
                            loadChildListData(allGuestJsonArray, 1);
                            //存储缓存
                            mACache.put(GuestListUtil.ALL_GUEST_JSON_ARRAY_CACHE,
                                    allGuestJsonArray, GuestListUtil.SAVE_TIME);
                        }
                        if (myGuestJsonArray != null && allGuestJsonArray != null) {
                            //数据更新完成
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        String name = (String) guestChildList.get(groupPosition).get(childPosition).get("name");
        Intent guestInfoIntent = new Intent(getContext(), GuestInfoActivity.class);
        int guest_type = groupPosition;
        if (groupPosition == 1) {
            //判断是不是我的嘉宾
            for (int i = 0; i < guestChildList.get(0).size(); i++) {
                if (guestChildList.get(0).get(i).get("name").equals(name)) {
                    guest_type = 0;
                    break;
                }
            }
        }
        guestInfoIntent.putExtra("guest_type", guest_type);
        guestInfoIntent.putExtra("gname", name);
        startActivity(guestInfoIntent);
        return false;
    }
}
