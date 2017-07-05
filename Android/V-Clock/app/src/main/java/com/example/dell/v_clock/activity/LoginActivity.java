package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.v_clock.R;

/**
 * This activity is the interface for the stuff to login which can jump to RegisterActivity.
 * 这个活动是工作人员登录的接口，能够跳转到注册界面。
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //“下一步”按钮
    private Button bt_next;
    //“注册”文字
    private TextView tv_sign_up;
    //手机号输入框
    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        bt_next = (Button) findViewById(R.id.bt_next);
        tv_sign_up = (TextView) findViewById(R.id.tv_sign_up);
        et_phone = (EditText) findViewById(R.id.edit_text_phone);

        //为控件设置监听器
        bt_next.setOnClickListener(this);
        tv_sign_up.setOnClickListener(this);

    }

    /**
     * 实现OnClickListener接口的onClick监听方法
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            //点击“下一步”按钮后的操作
            case R.id.bt_next:
                operateAfterNext();
                break;
            //点击“注册”文字后的操作
            case R.id.tv_sign_up:
                //通过Intent对象跳转到注册界面
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            //默认操作
            default:
                break;
        }
    }

    /**
     * 点击“下一步”之后的一系列操作
     */
    private void operateAfterNext() {
        //检测手机号输入的长度是否合适
        //在布局设置中已经设置过只能输入数字，且最多11位，所以只需检测数字是否足够11位即可
        String phoneNumber = et_phone.getText().toString();
        int lengthOfPhone = phoneNumber.length();
        if(lengthOfPhone < 11)
        {
            Toast.makeText(LoginActivity.this,"请检查您的手机号是否输入正确！",Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 向服务器查询输入手机号是否已注册

        //跳转到人脸识别界面
        Intent intent = new Intent(LoginActivity.this,CameraActivity.class);
        startActivity(intent);
    }
}
