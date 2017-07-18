package com.example.dell.v_clock.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import com.example.dell.v_clock.util.GuestListUtil;
import com.example.dell.v_clock.util.ImageUtil;
import com.example.dell.v_clock.util.JSONObjectRequestMapParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GuestInfoActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 界面中的控件
     */
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
    //该嘉宾姓名
    String guest_name;
    //该嘉宾图片
    Bitmap guest_photo;
    Boolean isPhotoChanged = false;
    //    //嘉宾信息对象
    GuestInfo guestInfo;
    //用户ID
    String eid;
    //代表性别的下标
    int sexIndex = 0;
    //修改后的性别
    String newSex = "";
    //请求对象、队列
    JSONObjectRequestMapParams searchRequest;
    RequestQueue requestQueue;
    //嘉宾的类型  我的嘉宾 or 全部嘉宾
    int guest_type;
    final int MY_GUEST = 0;
    final int ALL_GUEST = 1;

    //请求类型
    final String WHOLE_NAME_SEARCH_TYPE = "2";
    //要修改项的请求码
    final int COMPANY_REQUEST_CODE = 1;
    final int PHONE_REQUEST_CODE = 2;
    final int PHOTO_REQUEST_CODE = 3;
    final int CROP_REQUEST_CODE = 4;
    //申请read权限
//    final int MY_PERMISSION_REQUEST_READ = 0;
    private final String TAG = "GuestInfoActivity";

    //修改性别选择框
    AlertDialog.Builder sexDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_info);
        //初始化控件
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
        //为控件添加监听器
        img_bt_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        relative_company.setOnClickListener(this);
        relative_sex.setOnClickListener(this);
        relative_phone.setOnClickListener(this);
        //获取用户ID
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        eid = sp.getString("eid", null);
        //加载嘉宾信息
        loadGuestInfo();

        //TODO 测试ACache
//        ACache mACache = ACache.get(this);
//        Log.i("GuestInfoActivity",mACache.getAsString("test"));
    }

    /**
     * 加载嘉宾信息
     */
    private void loadGuestInfo() {
        //从上个Activity中，通过Intent获取嘉宾姓名、类型
        guest_name = getIntent().getStringExtra("gname");
//        Bundle temp = getIntent().getExtras();
//        guest_photo = temp.getParcelable("gphoto");
        guest_type = getIntent().getIntExtra("guest_type", MY_GUEST);

//        if (guest_photo == null) {
//         Log.i("GuestInfoActivity","guest_photo = null");
//        }

//        iv_photo.setImageBitmap(guest_photo);
        tv_name.setText(guest_name);

        //发送查询嘉宾信息的请求
        Map<String, String> searchInfo = new HashMap<>();
        searchInfo.put("gname", guest_name);
        searchInfo.put("tip", WHOLE_NAME_SEARCH_TYPE);
        searchInfo.put("eid", eid);
        searchRequest = new JSONObjectRequestMapParams(Request.Method.POST, ServerInfo.SEARCH_GUEST_URL, searchInfo,
                new GuestInfoResponseListener(), new GuestInfoResponseErrorListener());
        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(this);
        //刷新数据
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GuestListUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "当前网络不可用!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        requestQueue.add(searchRequest);
    }

    //捕捉数据更新完成的信息
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("GuestInfoActivity", "msg.what = " + msg.what);

            if (msg.what == 0) {
                //更改UI
                iv_photo.setImageBitmap(guestInfo.getGuestBitmapPhoto());
                guest_photo = guestInfo.getGuestBitmapPhoto();
                tv_name.setText(guestInfo.getGuestName());
                tv_phone.setText(guestInfo.getGuestPhone());
                tv_company.setText(guestInfo.getGuestCompany());
                tv_sex.setText(guestInfo.getGuestSex());
                newSex = guestInfo.getGuestSex();
                if (guestInfo.getGuestSex().equals("女")) {
                    sexIndex = 0;
                } else {
                    sexIndex = 1;
                }
                //根据嘉宾类型 确定操作的类型
                if (guest_type == MY_GUEST) {
                    tv_add.setText("移除");
                } else if (guest_type == ALL_GUEST) {
                    tv_add.setText("添加");
                }

            }

        }
    };

    /**
     * 监听各种点击事件
     *
     * @param view 点击控件
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_bt_info_back) {//返回上个页面
            finish();
        }
        if (guestInfo == null) {
            return;
        }
        switch (view.getId()) {
//            case R.id.img_bt_info_back://返回上个页面
//                finish();
//                break;
            case R.id.tv_guest_add://根据嘉宾类型 选择不同操作
                if (guest_type == MY_GUEST) {
                    //将该嘉宾从我的嘉宾中移除
                    deleteGuest();
                } else if (guest_type == ALL_GUEST) {
                    //将该嘉宾添加至我的嘉宾
                    addToGuest();
                }
                break;
            case R.id.iv_guest_info_photo:
                //为嘉宾选择新的照片
                modifyGuestPhoto();
                break;
            case R.id.relative_company:
                //跳转到修改嘉宾单位信息
                Intent companyIntent = new Intent(this, ModifyActivity.class);
                companyIntent.putExtra("modify_title", "工作单位");
                companyIntent.putExtra("modify_type", "gcompany");
                companyIntent.putExtra("guest_name", guest_name);
                companyIntent.putExtra("modify_content", guestInfo.getGuestCompany());
                startActivityForResult(companyIntent, COMPANY_REQUEST_CODE, null);
                break;
            case R.id.relative_sex:
                //修改嘉宾性别
                sexDialog = new AlertDialog.Builder(this);
                sexDialog.setTitle("性别");
                sexDialog.setSingleChoiceItems(new String[]{"女", "男"}, sexIndex, new SexDialogOnClickListener());
                sexDialog.setCancelable(true);
                sexDialog.show();
                break;
            case R.id.relative_phone:
                //跳转到修改嘉宾手机信息
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
     * 修改嘉宾照片
     */
    private void modifyGuestPhoto() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        intentFromGallery.setType("image/*");
        startActivityForResult(intentFromGallery, PHOTO_REQUEST_CODE);
    }

    /**
     * 性别选择框 点击监听器
     */
    private class SexDialogOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Log.i("GuestInfoActivity", "index = " + i);
            if (i == 0) {
                //点击了“女”
                if (tv_sex.getText().toString().equals("女")) {
                    dialogInterface.cancel();
                    return;
                }
                newSex = "女";
            } else if (i == 1) {
                //点击了“男”
                if (tv_sex.getText().toString().equals("男")) {
                    dialogInterface.cancel();
                    return;
                }
                newSex = "男";
            }
            //发送修改信息
            final Map<String, String> modifyMap = new HashMap<>();
            modifyMap.put("tip", "regid;gsex");
            modifyMap.put("gname", guest_name);
            modifyMap.put("gsex", newSex);
            modifyMap.put("regid", eid);
            StringRequest modifyRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_GUEST_INFO_URL,
                    new ModifyResponseListener(), new GuestInfoResponseErrorListener()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return modifyMap;
                }
            };
            requestQueue.add(modifyRequest);
            //取消对话框
            dialogInterface.cancel();
        }
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
                    Toast.makeText(GuestInfoActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    tv_sex.setText(newSex);
                    if (newSex.equals("女")) {
                        sexIndex = 0;
                    } else {
                        sexIndex = 1;
                    }
                    if (isPhotoChanged) {
                        //修改内存信息
                        GuestInfo guestInfo = new GuestInfo(guest_name, guest_photo);
                        guestInfo.setGuest_type(guest_type);
                        GuestListUtil.modifyPhoto(guestInfo, GuestInfoActivity.this);
                        isPhotoChanged = false;
                    }
                    break;
                case 1:
                    //修改失败
                    Toast.makeText(GuestInfoActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    break;
            }

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
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        返回数据
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
                case PHOTO_REQUEST_CODE://返回相册选择的图片
                    //剪裁图片
                    Log.i(TAG,"返回了选择的图片");
                    ImageUtil.startPhotoZoom(data.getData(), this, CROP_REQUEST_CODE);
                    break;
                case CROP_REQUEST_CODE://返回剪裁后的图片
                    //更改显示、上传图片
                    Log.i(TAG,"返回了剪裁的图片");
                    transferPhoto();
                    break;
            }
        }
    }

    /**
     * 更改显示、上传图片
     */
    private void transferPhoto() {
        guest_photo = ImageUtil.getCropImage(this);
        if (guest_photo == null) {
            return;
        }
        Log.i("GuestInfoActivity", "更改图片");
        iv_photo.setImageBitmap(guest_photo);
        isPhotoChanged = true;
        //传输图片
        String str_photo = ImageUtil.convertImage(guest_photo);
//        Log.i("GuestInfoActivity", "bmp_photo.length" + str_photo.length());
        //发送修改信息
        final Map<String, String> modifyMap = new HashMap<>();
        modifyMap.put("tip", "regid;gphoto");
        modifyMap.put("gname", guest_name);
        modifyMap.put("gphoto", str_photo);
        modifyMap.put("regid", eid);
        StringRequest modifyRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_GUEST_INFO_URL,
                new ModifyResponseListener(), new GuestInfoResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return modifyMap;
            }
        };
        requestQueue.add(modifyRequest);
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
                    //TODO   隔一段时间再刷新
//                    refreshData();
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
                //TODO   隔一段时间再刷新
//                refreshData();
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
            //todo  隔一段时间再刷新
//            refreshData();
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
                    GuestInfo guestInfo = new GuestInfo(guest_name, guest_photo);
                    if (guest_type == MY_GUEST) {
                        //移除成功
                        Toast.makeText(GuestInfoActivity.this, "已从我的嘉宾中移除", Toast.LENGTH_LONG).show();
                        guest_type = ALL_GUEST;
                        tv_add.setText("添加");
                        //更改内存中的GuestList
                        GuestListUtil.deleteFromMyGuest(guestInfo, GuestInfoActivity.this);
                    } else if (guest_type == ALL_GUEST) {
                        //添加成功
                        Toast.makeText(GuestInfoActivity.this, "已添加至我的嘉宾", Toast.LENGTH_LONG).show();
                        guest_type = MY_GUEST;
                        tv_add.setText("移除");
                        //更改内存中的GuestList
                        GuestListUtil.addToMyGuest(guestInfo, GuestInfoActivity.this);
                    }
                    //todo 本地缓存

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
