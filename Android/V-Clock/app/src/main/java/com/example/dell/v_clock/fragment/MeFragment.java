package com.example.dell.v_clock.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dell.v_clock.R;
import com.example.dell.v_clock.activity.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * The fragment shows the detail of one stuff.
 * 这个碎片展示了一位工作人员的详细信息
 */
public class MeFragment extends Fragment {


    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        Button signOut = (Button)view.findViewById(R.id.bt_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("eid");
                editor.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;
    }

}
