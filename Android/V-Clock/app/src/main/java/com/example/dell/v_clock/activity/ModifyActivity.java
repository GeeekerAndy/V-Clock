package com.example.dell.v_clock.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modigy);
        img_bt_back = (ImageButton) findViewById(R.id.img_bt_modify_back);
        tv_modify_title= (TextView) findViewById(R.id.tv_modify_info);
        tv_save = (TextView) findViewById(R.id.tv_save);
        et_modify_content= (EditText) findViewById(R.id.et_modify_content);

        img_bt_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        //确定修改内容
        commit_content();
    }

    /**
     * 确定修改信息  更新UI
     */
    private void commit_content() {
        modify_type= getIntent().getStringExtra("modify_type");
        modify_content = getIntent().getStringExtra("modify_content");
        guest_name = getIntent().getStringExtra("guest_name");
        tv_modify_title.setText(modify_type);
        et_modify_content.setText(modify_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_save:
                saveInfo();
                break;
            case R.id.img_bt_modify_back:
                finish();
                break;
        }

    }

    /**
     * 保存修改信息
     */
    private void saveInfo() {
        String info = et_modify_content.getText().toString();
        if(info.equals(modify_content))
        {
            Toast.makeText(this, "您并未修改信息！", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 传输修改信息  如果该嘉宾不是没有加入“我的嘉宾”列表 则加入
        final Map<String,String> modifyMap = new HashMap<>();
        modifyMap.put("tip","");
        modifyMap.put("name",guest_name);
        modifyMap.put(modify_type,info);
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String regid = sp.getString("eid", null);
        modifyMap.put("regid",regid);
        StringRequest loginRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_GUEST_INFO_URL,
                new ModifyResponseListener(), new ModifyResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return modifyMap;
            }
        };
        //访问服务器请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);
    }
    /**
     *
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
     *
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
