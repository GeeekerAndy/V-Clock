package com.example.dell.v_clock.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.dell.v_clock.R;
import com.example.dell.v_clock.activity.AddGuestActivity;
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.adapter.GuestListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        initGuestList(view);

        return view;
    }

    /**
     * 初始化嘉宾列表
     *
     * @param view
     */
    private void initGuestList(View view) {
        //TODO　向服务器发送请求  请求我的嘉宾 全部嘉宾
        //测试 自定义数据
        guestGroupList = new ArrayList<>();
        guestGroupList.add("我的嘉宾");
        guestGroupList.add("全部嘉宾");

        guestChildList = new ArrayList<>();

        //设置适配器
        guestListAdapter = new GuestListAdapter(this.getContext(), guestGroupList, guestChildList);
        guestList.setAdapter(guestListAdapter);

        //加载ChildList数据
        refreshChildList();
    }

    /**
     * 刷新ChildList的数据
     */
    private void refreshChildList() {

        guestChildList.clear();
        //TODO 测试显示效果  自定义搜索结果
        List< Map<String, Object>> tempList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> tempMap = new HashMap();
            tempMap.put("avatar", BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            tempMap.put("name", "小明" + i);
            tempList.add(tempMap);
            Log.i("循环加载数据",i+"");
        }
        guestChildList.add(tempList);

        List< Map<String, Object>> tempList2 = new ArrayList<>();
        for(int i = 0;i < 20;i++)
        {
            Map<String,Object> tempMap = new HashMap();
            tempMap.put("avatar",BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            tempMap.put("name","小红"+i);
            tempList2.add(tempMap);
        }
        guestChildList.add(tempList2);

        //数据改变 刷新UI
        guestListAdapter.notifyDataSetChanged();
    }

    /**
     * 搜索按钮点击事件的监听 TODO item右侧按钮的监听
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
     * @param expandableListView
     * @param view
     * @param i
     * @param i1
     * @param l
     * @return
     */
    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

        return false;
    }


}
