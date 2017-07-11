package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.example.dell.v_clock.object.GuestInfo;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    GuestInfo guestInfo;

    String eid;

    JSONObjectRequestMapParams searchRequest;
    RequestQueue requestQueue;

    int guest_type;
    final int MY_GUEST = 0;
    final int ALL_GUEST = 1;

    //单个嘉宾详细信息
    final String WHOLE_NAME_SEARCH_TYPE = "2";

    final int COMPANY_REQUEST_CODE = 1;
    final int PHONE_REQUEST_CODE = 2;
    final int PHOTO_REQUEST_CODE = 3;

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

        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);

        //加载嘉宾信息
        loadGuestInfo();
    }

    /**
     * 加载嘉宾信息
     */
    private void loadGuestInfo() {
        guest_name = getIntent().getStringExtra("gname");
        guest_type = getIntent().getIntExtra("guest_type", MY_GUEST);

        Log.i("GuestInfoActivity", "guest_type = " + guest_type);
        Log.i("GuestInfoActivity", "guest_name = " + guest_name);

        //发送请求
        Map<String, String> searchInfo = new HashMap<>();
        searchInfo.put("gname", guest_name);
        searchInfo.put("tip", WHOLE_NAME_SEARCH_TYPE);
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String eid = sp.getString("eid", null);
        searchInfo.put("eid", eid);
        searchRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, searchInfo,
                new GuestInfoResponseListener(), new GuestInfoResponseErrorListener());
        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(this);
        //刷新数据
        refreshData();
    }

    private void refreshData() {
        requestQueue.add(searchRequest);
    }

    //捕捉数据更新完成的信息
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO 更改UI
            iv_photo.setImageBitmap(guestInfo.getGuestBitmapPhoto());
            tv_name.setText(guestInfo.getGuestName());
            tv_phone.setText(guestInfo.getGuestPhone());
            tv_company.setText(guestInfo.getGuestCompany());
            tv_sex.setText(guestInfo.getGuestSex());
            //根据嘉宾类型 确定操作的类型
            if (guest_type == MY_GUEST) {
                tv_add.setText("移除");
            } else if (guest_type == ALL_GUEST) {
                tv_add.setText("添加");
            }

        }
    };

    /**
     * 监听各种点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (guestInfo == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.img_bt_info_back:
                finish();
                break;
            case R.id.tv_guest_add:
                if (guest_type == MY_GUEST) {
                    //将该嘉宾从我的嘉宾中移除
                    deleteGuest();
                } else if (guest_type == ALL_GUEST) {
                    //将该嘉宾添加至我的嘉宾
                    addToGuest();
                }
                break;
            case R.id.iv_guest_info_photo:
                //TODO 为嘉宾选择新的照片

                break;
            case R.id.relative_company:
                //跳转到修改嘉宾信息
                Intent companyIntent = new Intent(this, ModifyActivity.class);
                companyIntent.putExtra("modify_title", "工作单位");
                companyIntent.putExtra("modify_type", "gcompany");
                companyIntent.putExtra("guest_name", guest_name);
                companyIntent.putExtra("modify_content", guestInfo.getGuestCompany());
                startActivityForResult(companyIntent, COMPANY_REQUEST_CODE, null);
                break;
            case R.id.relative_sex:
                //修改嘉宾性别 TODO dialog？

                break;
            case R.id.relative_phone:
                //跳转到修改嘉宾信息
                Intent phoneIntent = new Intent(this, ModifyActivity.class);
                phoneIntent.putExtra("modify_title", "手机");
                phoneIntent.putExtra("modify_type", "gtel");
                phoneIntent.putExtra("guest_name", guest_name);
                phoneIntent.putExtra("modify_content", guestInfo.getGuestPhone());
                startActivityForResult(phoneIntent, PHONE_REQUEST_CODE, null);
                break;
        }
    }

    /**
     * 将该嘉宾添加至我的嘉宾
     */
    private void addToGuest() {
        //添加至“我的嘉宾”
        StringRequest addRequest = new StringRequest(Request.Method.POST, ServerInfo.ADD_TO_GUEST_LIST_URL,
                new AlterMyGuestResponseListener(), new AlterGuestResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> myGuestInfoMap = new HashMap<>();
                myGuestInfoMap.put("gname", guest_name);
                myGuestInfoMap.put("eid", eid);
                return myGuestInfoMap;
            }
        };
        requestQueue.add(addRequest);
    }

    /**
     * 将该嘉宾从我的嘉宾中移除
     */
    private void deleteGuest() {
        //从“我的嘉宾”中移除
        StringRequest addRequest = new StringRequest(Request.Method.POST, ServerInfo.DELETE_FROM_GUEST_LIST_URL,
                new AlterMyGuestResponseListener(), new AlterGuestResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> myGuestInfoMap = new HashMap<>();
                myGuestInfoMap.put("gname", guest_name);
                myGuestInfoMap.put("eid", eid);
                return myGuestInfoMap;
            }
        };
        requestQueue.add(addRequest);
    }

    /**
     * 从修改界面返回后的处理 更新信息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case COMPANY_REQUEST_CODE:
                    tv_company.setText(data.getStringExtra("gcompany"));
                    break;
                case PHONE_REQUEST_CODE:
                    tv_phone.setText(data.getStringExtra("gtel"));
                    break;
                case PHOTO_REQUEST_CODE:

                    break;
            }
        }
    }

    /**
     * 请求嘉宾信息的监听器
     */
    private class GuestInfoResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            //判断返回是否有效
            try {
                String tip = response.getString("tip");
                if (tip.equals("2")) {
                    //数据错误
                    Toast.makeText(GuestInfoActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                    refreshData();
                    return;
                } else if (tip.equals("0")) {
                    //接收成功
                    Log.i("Search", "接收成功");
                    String name = response.getString("gname");
                    String sex = response.getString("gsex");
                    String phone = response.getString("gtel");
                    String company = response.getString("gcompany");
                    String basePhoto = response.getString("gphoto");
                    Bitmap photo = ImageUtil.convertImage(basePhoto);
                    guestInfo = new GuestInfo(name, sex, company, phone, photo);
                    //发送Message 更新UI
                    handler.sendEmptyMessage(0);
                }

            } catch (JSONException e) {
//                Toast.makeText(GuestInfoActivity.this, "该嘉宾未添加！", Toast.LENGTH_SHORT).show();
                refreshData();
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求嘉宾信息的错误监听器
     */
    private class GuestInfoResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(GuestInfoActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
            refreshData();
        }
    }

    /**
     * 更改嘉宾所属分组的监听器
     */
    private class AlterMyGuestResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            Log.i("Transfer", "收到服务器回复");
            int intOfResponse = -1;
            try {
                intOfResponse = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                //返回数据包含非数字信息
                Log.i("GuestInfoTransfer", "收到服务器回复 数据错误");
                Log.i("AddGuest", "response 包含非数字信息");
                e.printStackTrace();
            }
            switch (intOfResponse) {
                case 0:
                    if (guest_type == MY_GUEST) {
                        //移除成功
                        Toast.makeText(GuestInfoActivity.this, "已从我的嘉宾中移除", Toast.LENGTH_LONG).show();
                        guest_type = ALL_GUEST;
                        tv_add.setText("添加");
                    } else if (guest_type == ALL_GUEST) {
                        //添加成功
                        Toast.makeText(GuestInfoActivity.this, "已添加至我的嘉宾", Toast.LENGTH_LONG).show();
                        guest_type = MY_GUEST;
                        tv_add.setText("移除");
                    }
                    break;
                case 1:
                    if (guest_type == MY_GUEST) {
                        //此嘉宾不存在我的嘉宾中
                        Toast.makeText(GuestInfoActivity.this, "此嘉宾不在我的嘉宾中！", Toast.LENGTH_SHORT).show();
                    } else if (guest_type == ALL_GUEST) {
                        //此嘉宾已存在
                        Toast.makeText(GuestInfoActivity.this, "此嘉宾已在我的嘉宾中！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    //数据错误
                    Toast.makeText(GuestInfoActivity.this, "数据传输错误，没有添加至我的嘉宾中！", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    /**
     * 更改嘉宾所属分组的错误监听器
     */
    private class AlterGuestResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(GuestInfoActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }
}
