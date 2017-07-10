package com.example.dell.v_clock.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.activity.AddGuestActivity;
import com.example.dell.v_clock.activity.GuestInfoActivity;
import com.example.dell.v_clock.activity.SearchActivity;
import com.example.dell.v_clock.adapter.GuestListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
        initGuestList();

        return view;
    }

    /**
     * 初始化嘉宾列表
     */
    private void initGuestList() {
        //GroupList只包含两项
        guestGroupList = new ArrayList<>();
        guestGroupList.add("我的嘉宾");
        guestGroupList.add("全部嘉宾");
        //childList的信息来源于后台服务器
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
        //TODO　向服务器发送请求  请求我的嘉宾 全部嘉宾

        GuestListRequest customRequest = new GuestListRequest(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL,null,
                new GuestListResponseListener(), new GuestListResponseErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //TODO　要传哪些参数
                Map<String,String> searchInfo = new  HashMap<>();
//                searchInfo.put("gname",name);
//                searchInfo.put("tip",WHOLE_NAME_SEATCH_TYPE);
//                SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
//                String eid = sp.getString("eid", null);
//                searchInfo.put("eid",eid);
                return searchInfo;
            }
        };
        //访问服务器请求队列
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(customRequest);


        //刷新数据时 首先清空原来的数据  收到服务器正确回复再 clear（）
        guestChildList.clear();

        //TODO 测试显示效果  自定义搜索结果
        List<Map<String, Object>> tempList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> tempMap = new HashMap();
            tempMap.put("avatar", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            tempMap.put("name", "小明" + i);
            tempList.add(tempMap);
            Log.i("循环加载数据", i + "");
        }
        guestChildList.add(tempList);

        List<Map<String, Object>> tempList2 = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> tempMap = new HashMap();
            tempMap.put("avatar", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            tempMap.put("name", "小红" + i);
            tempList2.add(tempMap);
        }
        guestChildList.add(tempList2);

        //数据改变 刷新UI
        guestListAdapter.notifyDataSetChanged();
    }

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
        //TODO 判断点击的是哪一项

//        Log.i("GuestListFrament","点击了item");
        Intent guestInfoIntent = new Intent(getContext(), GuestInfoActivity.class);
        startActivity(guestInfoIntent);
        return false;
    }


    /******************************************************************************
     * 暂时使用内部类获取 后台信息
     ********************************************************************************/
    /**
     * 接收Json对象的Request类
     */
    private class GuestListRequest extends Request<JSONObject> {

        private Response.Listener<JSONObject> listener;
        private Map<String, String> params;

        public GuestListRequest(String url, Map<String, String> params,
                                Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            this.listener = reponseListener;
            this.params = params;
        }

        public GuestListRequest(int method, String url, Map<String, String> params,
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

    /**
     *
     */
    private class GuestListResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //TODO 判断返回是否有效
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
        }
    }

}
