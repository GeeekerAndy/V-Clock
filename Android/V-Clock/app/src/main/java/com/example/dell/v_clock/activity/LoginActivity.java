package com.example.dell.v_clock.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dell.v_clock.R;

/**
 * This activity is the interface for the stuff to login which can jump to RegisterActivity.
 * 这个活动是工作人员登录的接口，能够跳转到注册界面。
*/

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
