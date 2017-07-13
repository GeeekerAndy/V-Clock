package com.example.dell.v_clock.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.v_clock.R;
import com.org.afinal.simplecache.ACache;

import java.security.acl.Group;
import java.util.List;
import java.util.Map;

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

    public GuestListAdapter(Context context, List<String> groupList, List<List<Map<String, Object>>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
    }


    @Override
    public int getGroupCount() {
//        Log.i(TAG,"getGroupCount "+groupList.size());
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        Log.i(TAG,"getChildrenCount "+childList.get(groupPosition).size()+" groupPosition "+groupPosition);
        if (childList.size() <= groupPosition) {
            return 0;
        }
        return childList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
//        Log.i(TAG,"getGroup");
        if (groupList.size() <= groupPosition) {
            return null;
        }
        return groupList.get(groupPosition);
    }

    @Override
    public Map<String, Object> getChild(int groupPosition, int childPosition) {
//        Log.i(TAG,"getChild");
        if (childList.size() <= groupPosition) {
            return null;
        } else if (childList.get(groupPosition).size() < childPosition) {
            return null;
        }
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
//        Log.i(TAG,"getGroupId " +i);
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
//        Log.i(TAG,"getChildId" + i1);
        return i1;
    }

    @Override
    public boolean hasStableIds() {
//        Log.i(TAG,"hasStableIds");
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
//        Log.i(TAG,"getGroupView");
        convertView = View.inflate(context, R.layout.item_group, null);
        //分组名称
        TextView groupName = convertView.findViewById(R.id.tv_group_name);
        //子元素个数
        TextView groupNum = convertView.findViewById(R.id.tv_guest_num);

        groupName.setText(groupList.get(groupPosition));
//        Log.i("GuestListAdapter", "groupPosition = " + groupPosition + " childList.size = " + childList.size());
        if (childList.size() > groupPosition) {
            String num = childList.get(groupPosition).size() + "";
            groupNum.setText(num);
        } else {
            groupNum.setText(0 + "");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
//        Log.i(TAG,"getChildView");
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

        iv_avatar.setImageBitmap((Bitmap) childList.get(groupPosition).get(childPosition).get("avatar"));
        tv_name.setText(childList.get(groupPosition).get(childPosition).get("name").toString());
        img_bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.img_bt_cross_gray:
                        Log.i("GuestAdapter", "点击了叉号");
                        break;
                    case R.id.img_bt_plus_gray:
                        Log.i("GuestAdapter", "点击了加号");
                        break;
                }
            }
        });

        //todo test
        ACache mACache = ACache.get(context);
        Log.i("GuestListAdapter", "ACache test = " + mACache.getAsString("test"));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
//        Log.i(TAG,"isChildSelectable");
        return true;
    }
}
