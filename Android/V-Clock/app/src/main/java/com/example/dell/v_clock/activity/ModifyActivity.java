package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.v_clock.R;
import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.util.CheckLegality;
import com.example.dell.v_clock.util.GuestListUtil;

import java.util.HashMap;
import java.util.Map;

public class ModifyActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton img_bt_back;
    TextView tv_modify_title;
    TextView tv_save;
    EditText et_modify_content;

    String modify_content;
    String modify_type;
    String guest_name;

    String afterModifyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        img_bt_back = (ImageButton) findViewById(R.id.img_bt_modify_back);
        tv_modify_title = (TextView) findViewById(R.id.tv_modify_info);
        tv_save = (TextView) findViewById(R.id.tv_save);
        et_modify_content = (EditText) findViewById(R.id.et_modify_content);

        img_bt_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        //确定修改内容
        commit_content();
    }

    /**
     * 确定修改信息  更新UI
     */
    private void commit_content() {
        modify_type = getIntent().getStringExtra("modify_type");
        if (modify_type.equals("gtel")) {
            //设置Input
            et_modify_content.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_modify_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        } else if (modify_type.equals("gcompany")) {
            et_modify_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }
        modify_content = getIntent().getStringExtra("modify_content");
        String title = getIntent().getStringExtra("modify_title");
        guest_name = getIntent().getStringExtra("guest_name");
        tv_modify_title.setText(title);
        et_modify_content.setText(modify_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                saveInfo();
                break;
            case R.id.img_bt_modify_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GuestListUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "当前网络不可用!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存修改信息
     */
    private void saveInfo() {
        afterModifyInfo = et_modify_content.getText().toString();
        if (afterModifyInfo.equals(modify_content)) {
            Toast.makeText(this, "您并未修改信息！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (modify_type.equals("gtel")) {
            if (!CheckLegality.isPhoneValid(afterModifyInfo)) {
                Toast.makeText(this, "手机格式填写不正确！", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (modify_type.equals("gcompany")) {
            if (afterModifyInfo.equals("")) {
                Toast.makeText(this, "单位不能为空！", Toast.LENGTH_SHORT).show();
                return;
            } else if (!CheckLegality.isNameContainSpace(afterModifyInfo)) {
                Toast.makeText(this, "单位不能包含空格！", Toast.LENGTH_SHORT).show();
                return;
            } else if (CheckLegality.isContainSpecialChar(afterModifyInfo)) {
                Toast.makeText(this, "单位不能包含特殊字符！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        final Map<String, String> modifyMap = new HashMap<>();
        modifyMap.put("tip", "regid;" + modify_type);
        modifyMap.put("gname", guest_name);
        modifyMap.put(modify_type, afterModifyInfo);
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String regid = sp.getString("eid", null);
        modifyMap.put("regid", regid);
        //测试代码
        Log.i("ModifyActivity", "tip:" + "regid;" + modify_type);
        Log.i("ModifyActivity", "gname:" + guest_name);
        Log.i("ModifyActivity", modify_type + ":" + afterModifyInfo);
        Log.i("ModifyActivity", "regid:" + regid);

        StringRequest modifyRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_GUEST_INFO_URL,
                new ModifyResponseListener(), new ModifyResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return modifyMap;
            }
        };
        //访问服务器请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(modifyRequest);
    }

    /**
     * 修改信息请求监听器
     */
    private class ModifyResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            Log.i("Transfer", "收到服务器回复");
            int intOfResponse = -1;
            try {
                intOfResponse = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                //返回数据包含非数字信息
                Log.i("modifyInfoTransfer", "收到服务器回复 数据错误");
                Log.i("AddGuest", "response 包含非数字信息");
                e.printStackTrace();
            }
            switch (intOfResponse) {
                case 0:
                    //修改成功
                    Toast.makeText(ModifyActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(modify_type, afterModifyInfo);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 1:
                    //修改失败
                    Toast.makeText(ModifyActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    /**
     * 修改信息请求错误监听器
     */
    private class ModifyResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(ModifyActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }
}
