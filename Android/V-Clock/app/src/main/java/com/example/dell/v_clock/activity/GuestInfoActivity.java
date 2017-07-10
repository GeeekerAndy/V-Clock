package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.v_clock.R;

public class GuestInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton img_bt_back;
    TextView tv_add;
    ImageView iv_photo;
    TextView tv_name;
    TextView tv_sex;
    TextView tv_company;
    TextView tv_phone;

    RelativeLayout relative_company;
    RelativeLayout relative_phone;
    RelativeLayout relative_sex;

    String guest_name;
    String guest_company;
    String guest_sex;
    String guest_phone;

    final int COMPANY_REQUEST_CODE = 1;
    final int PHONE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_info);

        img_bt_back = (ImageButton) findViewById(R.id.img_bt_info_back);
        tv_add = (TextView) findViewById(R.id.tv_guest_add);
        iv_photo = (ImageView) findViewById(R.id.iv_guest_info_photo);
        relative_company = (RelativeLayout) findViewById(R.id.relative_company);
        relative_phone = (RelativeLayout) findViewById(R.id.relative_phone);
        relative_sex = (RelativeLayout) findViewById(R.id.relative_sex);
        tv_name = (TextView) findViewById(R.id.tv_guest_info_name);
        tv_sex = (TextView) findViewById(R.id.tv_guest_info_sex);
        tv_company = (TextView) findViewById(R.id.tv_guest_info_company);
        tv_phone = (TextView) findViewById(R.id.tv_guest_info_phone);

        img_bt_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        relative_company.setOnClickListener(this);
        relative_sex.setOnClickListener(this);
        relative_phone.setOnClickListener(this);

        //加载嘉宾信息
        loadGuestInfo();
    }

    /**
     * 加载嘉宾信息
     */
    private void loadGuestInfo() {
        //TODO

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bt_info_back:
                finish();
                break;
            case R.id.tv_guest_add:
                //TODO 判断该嘉宾是不是“我的嘉宾” 不是则添加

                break;
            case R.id.iv_guest_info_photo:
                //TODO 为嘉宾选择新的照片

                break;
            case R.id.relative_company:
                //跳转到修改嘉宾信息
                Intent companyIntent = new Intent(this, ModifyActivity.class);
                companyIntent.putExtra("modify_type", "company");
                companyIntent.putExtra("guest_name", guest_name);
                companyIntent.putExtra("modify_content", guest_company);
                startActivityForResult(companyIntent, COMPANY_REQUEST_CODE, null);
                break;
            case R.id.relative_sex:
                //修改嘉宾性别 TODO dialog？

                break;
            case R.id.relative_phone:
                //跳转到修改嘉宾信息
                Intent phoneIntent = new Intent(this, ModifyActivity.class);
                phoneIntent.putExtra("modify_type", "phone");
                phoneIntent.putExtra("guest_name", guest_name);
                phoneIntent.putExtra("modify_content", guest_phone);
                startActivityForResult(phoneIntent, PHONE_REQUEST_CODE, null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
