package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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

    final String TAG = "MainActivity";

    final MessageListFragment messageListFragment = new MessageListFragment();
    final GuestListFragment guestListFragment = new GuestListFragment();
    final HistoryFragment historyFragment = new HistoryFragment();
    final MeFragment meFragment = new MeFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notifications:
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.ll_fragment_container, messageListFragment);
                    transaction1.commit();
                    return true;
                case R.id.navigation_guests:
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.ll_fragment_container, guestListFragment);
                    transaction2.commit();
                    return true;
                case R.id.navigation_history:
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.ll_fragment_container, historyFragment);
                    transaction3.commit();
                    return true;
                case R.id.navigation_me:
                    FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                    transaction4.replace(R.id.ll_fragment_container, meFragment);
                    transaction4.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.cancelPendingInputEvents();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ll_fragment_container, messageListFragment);
        transaction.commit();

        Intent startServiceIntent = new Intent(this, GetMessageService.class);
        startService(startServiceIntent);
    }
}
