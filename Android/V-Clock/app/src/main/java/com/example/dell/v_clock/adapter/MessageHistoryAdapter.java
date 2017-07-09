package com.example.dell.v_clock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.v_clock.R;
import com.example.dell.v_clock.object.GuestHistory;
import com.example.dell.v_clock.object.GuestMessage;
import com.example.dell.v_clock.util.ImageUtil;

import java.util.List;

/**
 * Created by andy on 7/8/17.
 */

public class MessageHistoryAdapter extends ArrayAdapter<GuestHistory> {

    private int historyLayoutID;

    public MessageHistoryAdapter(Context context, int layoutID, List<GuestHistory> guestHistoryList) {
        super(context, layoutID, guestHistoryList);
        this.historyLayoutID = layoutID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GuestHistory guestHistory = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(historyLayoutID, parent, false);
        TextView guestName = view.findViewById(R.id.tv_guest_name_in_message);
        TextView arriveTime = view.findViewById(R.id.tv_arrive_time_in_message);
        ImageView myGuestAvatar = view.findViewById(R.id.iv_my_guest_avatar);
        guestName.setText("嘉宾 " + guestHistory.getGuestName());
        arriveTime.setText(guestHistory.getArriveTime());
        myGuestAvatar.setImageBitmap(ImageUtil.convertImage(guestHistory.getBase64Pic()));
        return view;
    }
}
