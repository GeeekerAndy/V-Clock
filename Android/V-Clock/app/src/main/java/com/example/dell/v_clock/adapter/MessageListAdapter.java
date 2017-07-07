package com.example.dell.v_clock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dell.v_clock.R;
import com.example.dell.v_clock.object.GuestMessage;

import java.util.List;

/**
 * Created by andy on 7/5/17.
 */

public class MessageListAdapter extends ArrayAdapter<GuestMessage> {

    private int messageLayoutID;

    public MessageListAdapter(Context context, int layoutID, List<GuestMessage> guestList) {
        super(context, layoutID, guestList);
        messageLayoutID = layoutID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GuestMessage guestMessage = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(messageLayoutID, parent, false);
        TextView guestName = (TextView)view.findViewById(R.id.tv_guest_name_in_message);
        TextView arriveTime = (TextView)view.findViewById(R.id.tv_arrive_time_in_message);
        guestName.setText(guestMessage.getGuestName());
        arriveTime.setText(guestMessage.getArriveTime());
        return view;
    }

}
