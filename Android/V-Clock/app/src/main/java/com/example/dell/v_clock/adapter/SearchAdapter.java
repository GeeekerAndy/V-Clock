package com.example.dell.v_clock.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by 王庆伟 on 2017/7/11.
 */

public class SearchAdapter extends BaseAdapter {

    private String[] from;
    private int[] to;
    private List<Map<String, Object>> dataList_guest;
    private Context context;
    private int resource;
    private LayoutInflater layoutInflater;

    public SearchAdapter(Context context, List<Map<String, Object>> dataList_guest, int resource, String[] from, int[] to) {
        this.context = context;
        this.dataList_guest = dataList_guest;
        this.resource = resource;
        this.from = from;
        this.to = to;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList_guest.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList_guest.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(resource, null);
            for (int j = 0; j < dataList_guest.size(); j++) {
                ImageView avatar = view.findViewById(to[0]);
                TextView name = view.findViewById(to[1]);
                //
                avatar.setBackground(new BitmapDrawable((Bitmap) dataList_guest.get(j).get(from[0])));
                name.setText(dataList_guest.get(j).get(from[1]).toString());
            }

        } else {
            for (int j = 0; j < dataList_guest.size(); j++) {
                ImageView avatar = view.findViewById(to[0]);
                TextView name = view.findViewById(to[1]);
                avatar.setBackground(new BitmapDrawable((Bitmap) dataList_guest.get(j).get(from[0])));
                name.setText(dataList_guest.get(j).get(from[1]).toString());
            }
        }
        return view;
    }
}
