package com.example.dell.v_clock.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.v_clock.R;

/**
 * This fragment shows the list of my guest and all guest.
 * 这个碎片展示我的嘉宾和所有嘉宾
 */
public class GuestListFragment extends Fragment {


    public GuestListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_list, container, false);
    }

}
