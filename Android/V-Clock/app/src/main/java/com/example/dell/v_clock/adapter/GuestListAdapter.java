package com.example.dell.v_clock.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.object.GuestInfo;
import com.example.dell.v_clock.util.GuestListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 王庆伟 on 2017/7/8.
 */

public class GuestListAdapter extends BaseExpandableListAdapter {

    //测试
    String TAG = "GuestListAdapter";

    //外层的数据源
    private List<String> groupList;
    //内层数据源
    private List<List<Map<String, Object>>> childList;
    //上下文对象
    private Context context;

    //请求队列
    private RequestQueue requestQueue;
    String eid;

    public GuestListAdapter(Context context, List<String> groupList, List<List<Map<String, Object>>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
        requestQueue = Volley.newRequestQueue(context);
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);
    }


    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childList.size() <= groupPosition) {
            return 0;
        }
        return childList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        if (groupList.size() <= groupPosition) {
            return null;
        }
        return groupList.get(groupPosition);
    }

    @Override
    public Map<String, Object> getChild(int groupPosition, int childPosition) {
        if (childList.size() <= groupPosition) {
            return null;
        } else if (childList.get(groupPosition).size() <= childPosition) {
            return null;
        }
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {

        convertView = View.inflate(context, R.layout.item_group, null);
        //分组名称
        TextView groupName = convertView.findViewById(R.id.tv_group_name);
        //子元素个数
        TextView groupNum = convertView.findViewById(R.id.tv_guest_num);
        //避免越界
        if (childList.size() <= groupPosition) {
            return convertView;
        }
        groupName.setText(groupList.get(groupPosition));
//        Log.i("GuestListAdapter", "groupPosition = " + groupPosition + " childList.size = " + childList.size());
        if (childList.size() > groupPosition) {
            String num = childList.get(groupPosition).size() + "";
            groupNum.setText(num);
        } else {
            groupNum.setText("0");
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        ImageView iv_avatar;
        TextView tv_name;
        ImageButton img_bt_move;
        if (groupPosition == 0) {
            //“我的嘉宾”列表
            view = View.inflate(context, R.layout.item_children_my_guest, null);
            iv_avatar = view.findViewById(R.id.iv_my_guest_avatar);
            tv_name = view.findViewById(R.id.tv_my_guest_name);
            img_bt_move = view.findViewById(R.id.img_bt_cross_gray);
        } else {
            //“全部嘉宾”列表
            view = View.inflate(context, R.layout.item_children_all_guest, null);
            iv_avatar = view.findViewById(R.id.iv_all_guest_avatar);
            tv_name = view.findViewById(R.id.tv_all_guest_name);
            img_bt_move = view.findViewById(R.id.img_bt_plus_gray);
        }
        synchronized (this) {
            //避免越界
            if (childList.size() <= groupPosition) {
                return view;
            } else if (childList.get(groupPosition).size() <= childPosition) {
                return view;
            }
            iv_avatar.setImageBitmap((Bitmap) childList.get(groupPosition).get(childPosition).get("avatar"));
            tv_name.setText(childList.get(groupPosition).get(childPosition).get("name").toString());
        }
        img_bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Map<String, Object>> guestList = null;
                if (groupPosition == 0) {
                    guestList = GuestListUtil.getMyGuestList();
                } else if (groupPosition == 1) {
                    guestList = GuestListUtil.getAllGuestList();
                }
                if (groupList != null) {
                    String guest_name = (String) guestList.get(childPosition).get("name");
                    Bitmap guest_photo = (Bitmap) guestList.get(childPosition).get("avatar");
                    switch (view.getId()) {
                        case R.id.img_bt_cross_gray:
                            Log.i("GuestAdapter", "点击了叉号");
                            //从我的嘉宾删除
                            deleteFromMyGuest(eid, guest_name);
                            break;
                        case R.id.img_bt_plus_gray:
                            Log.i("GuestAdapter", "点击了加号");
                            //添加至我的嘉宾
                            addToMyGuest(eid, guest_name, guest_photo);
                            break;
                    }
                }
            }
        });
        return view;
    }

    /**
     * 从我的嘉宾删除
     *
     * @param eid        工作人员工号
     * @param guest_name 嘉宾姓名
     */
    private void deleteFromMyGuest(final String eid, final String guest_name) {
        //从“我的嘉宾”中移除
        StringRequest addRequest = new StringRequest(Request.Method.POST, ServerInfo.DELETE_FROM_GUEST_LIST_URL,
                new deleteMyGuestResponseListener(), new AlterGuestResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> myGuestInfoMap = new HashMap<>();
                myGuestInfoMap.put("gname", guest_name);
                myGuestInfoMap.put("eid", eid);
                return myGuestInfoMap;
            }
        };
        requestQueue.add(addRequest);
        //更新内存
        GuestInfo guestInfo =new GuestInfo(guest_name);
        GuestListUtil.deleteFromMyGuest(guestInfo);
    }

    /**
     * 添加至我的嘉宾
     *
     * @param eid        工作人员工号
     * @param guest_name 嘉宾姓名
     */
    private void addToMyGuest(final String eid, final String guest_name, final Bitmap guest_photo) {
        //添加至“我的嘉宾”
        StringRequest addRequest = new StringRequest(Request.Method.POST, ServerInfo.ADD_TO_GUEST_LIST_URL,
                new addMyGuestResponseListener(), new AlterGuestResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> myGuestInfoMap = new HashMap<>();
                myGuestInfoMap.put("gname", guest_name);
                myGuestInfoMap.put("eid", eid);
                return myGuestInfoMap;
            }
        };
        requestQueue.add(addRequest);
        //更新内存
        GuestInfo guestInfo =new GuestInfo(guest_name,guest_photo);
        GuestListUtil.addToMyGuest(guestInfo);
    }

    /**
     * 添加嘉宾所属分组的监听器
     */
    private class addMyGuestResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            Log.i("Transfer", "收到服务器回复");
            int intOfResponse = -1;
            try {
                intOfResponse = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                //返回数据包含非数字信息
                Log.i("GuestInfoTransfer", "收到服务器回复 数据错误");
                Log.i("AddGuest", "response 包含非数字信息");
                e.printStackTrace();
            }
            switch (intOfResponse) {
                case 0:
                    break;
                case 1:

                    break;
                case 2:
                    //数据错误
                    Toast.makeText(context, "数据传输错误", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    /**
     * 删除嘉宾所属分组的监听器
     */
    private class deleteMyGuestResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            Log.i("Transfer", "收到服务器回复");
            int intOfResponse = -1;
            try {
                intOfResponse = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                //返回数据包含非数字信息
                Log.i("GuestInfoTransfer", "收到服务器回复 数据错误");
                Log.i("AddGuest", "response 包含非数字信息");
                e.printStackTrace();
            }
            switch (intOfResponse) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    //数据错误
                    Toast.makeText(context, "数据传输错误！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 更改嘉宾所属分组的错误监听器
     */
    private class AlterGuestResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(context, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
