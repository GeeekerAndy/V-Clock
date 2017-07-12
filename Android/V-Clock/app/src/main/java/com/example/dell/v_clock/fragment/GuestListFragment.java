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
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.adapter.GuestListAdapter;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;

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
public class GuestListFragment extends Fragment implements View.OnClickListener, ExpandableListView.OnChildClickListener {

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

    //我的嘉宾
    final String MY_GEUST_SEATCH_TYPE = "0";
    //全部嘉宾（gname="") 异步搜索
    final String PARTITIAL_NAME_SEATCH_TYPE = "1";

    JSONObjectRequestMapParams myGuestRequest;
    JSONObjectRequestMapParams allGuestRequest;
    RequestQueue requestQueue;


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
        //向服务器发送请求  请求我的嘉宾
        Map<String, String> my_searchInfo = new HashMap<>();
        my_searchInfo.put("tip", MY_GEUST_SEATCH_TYPE);
        SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        String eid = sp.getString("eid", null);
        my_searchInfo.put("eid", eid);
        myGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, my_searchInfo,
                new MyGuestListResponseListener(), new GuestListResponseErrorListener());
        //  请求全部嘉宾
        Map<String, String> all_searchInfo = new HashMap<>();
        all_searchInfo.put("gname", "");
        all_searchInfo.put("tip", PARTITIAL_NAME_SEATCH_TYPE);
        my_searchInfo.put("eid", eid);
        allGuestRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, all_searchInfo,
                new AllGuestListResponseListener(), new GuestListResponseErrorListener());

        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(getContext());
        refreshChildList();
    }

    /**
     * 刷新ChildList的数据
     */
    private void refreshChildList() {
        //发出请求
        requestQueue.add(myGuestRequest);
        requestQueue.add(allGuestRequest);
    }

    //捕捉数据更新完成的信息
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //数据改变 刷新UI
            guestListAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 搜索按钮点击事件的监听
     *
     * @param view
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
        guestInfoIntent.putExtra("guest_type",groupPosition);
        guestInfoIntent.putExtra("gname",name);
        startActivity(guestInfoIntent);
        return false;
    }

    /**
     *
     */
    private class MyGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("GuestList");
                //更新data
                if (guestChildList.size() > 0) {
                    guestChildList.get(0).clear();
                }
                List<Map<String, Object>> tempList = new ArrayList<>();
                for (int i = 0; i < jsonObjects.length(); i++) {
                    Map<String, Object> tempMap = new HashMap();
                    String basePhoto = jsonObjects.getJSONObject(i).getString("gphoto");
                    tempMap.put("avatar", ImageUtil.convertImage(basePhoto));
                    tempMap.put("name", jsonObjects.getJSONObject(i).getString("gname"));
                    tempList.add(tempMap);
                    //手机号暂时不用
//                    String phone = response.getString("gtel");
                }
                guestChildList.add(0, tempList);
                handler.sendEmptyMessage(0);
            } catch (JSONException e) {
                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private class AllGuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            JSONArray jsonObjects;
            try {
                jsonObjects = response.getJSONArray("Guest");
                //更新data
                if (guestChildList.size() > 1) {
                    guestChildList.get(1).clear();
                }
                List<Map<String, Object>> tempList = new ArrayList<>();
                for (int i = 0; i < jsonObjects.length(); i++) {
                    Map<String, Object> tempMap = new HashMap();
                    String basePhoto = jsonObjects.getJSONObject(i).getString("gphoto");
                    tempMap.put("avatar", ImageUtil.convertImage(basePhoto));
                    tempMap.put("name", jsonObjects.getJSONObject(i).getString("gname"));
                    tempList.add(tempMap);
                    //手机号暂时不用
//                    String phone = response.getString("gtel");
                }
                guestChildList.add(1, tempList);
                handler.sendEmptyMessage(1);
            } catch (JSONException e) {
                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private class GuestListResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(getContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
            //todo  隔一段时间再请求
//            refreshChildList();
        }
    }

}
