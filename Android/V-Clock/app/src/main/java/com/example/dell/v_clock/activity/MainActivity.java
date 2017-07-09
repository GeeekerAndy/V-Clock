package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.example.dell.v_clock.R;
import com.example.dell.v_clock.fragment.GuestListFragment;
import com.example.dell.v_clock.fragment.HistoryFragment;
import com.example.dell.v_clock.fragment.MeFragment;
import com.example.dell.v_clock.fragment.MessageListFragment;
import com.example.dell.v_clock.service.GetMessageService;

/**
 * This is the main interface, including four parts Messages, GuestList, History and Me.
 * 这是程序的主界面, 包括消息，嘉宾列表，到达记录和我的四个部分。
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MessageListFragment messageListFragment = new MessageListFragment();
        final GuestListFragment guestListFragment = new GuestListFragment();
        final HistoryFragment historyFragment = new HistoryFragment();
        final MeFragment meFragment = new MeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ll_fragment_container, messageListFragment);
        transaction.commit();

        RadioButton radioMessage = (RadioButton) findViewById(R.id.rb_message);
        radioMessage.setChecked(true);
        radioMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.ll_fragment_container, messageListFragment);
                transaction1.commit();
            }
        });
        RadioButton  radioGuestList = (RadioButton) findViewById(R.id.rb_guest_list);
        radioGuestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.ll_fragment_container, guestListFragment);
                transaction2.commit();
            }
        });
        RadioButton radioHistory = (RadioButton) findViewById(R.id.rb_history);
        radioHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                transaction3.replace(R.id.ll_fragment_container, historyFragment);
                transaction3.commit();
            }
        });
        RadioButton radioMe = (RadioButton) findViewById(R.id.rb_me);
        radioMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                transaction4.replace(R.id.ll_fragment_container, meFragment);
                transaction4.commit();
            }
        });
        Intent startServiceIntent = new Intent(this, GetMessageService.class);
        startService(startServiceIntent);
    }

}
