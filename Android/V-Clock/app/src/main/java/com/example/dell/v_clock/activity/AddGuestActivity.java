package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.example.dell.v_clock.util.CheckLegality;
import com.example.dell.v_clock.util.GuestListUtil;
import com.example.dell.v_clock.util.ImageUtil;

import java.util.HashMap;
import java.util.Map;

import static android.os.SystemClock.sleep;

public class AddGuestActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PICK_PHOTO_FOR_AVATAR = 1;
    final int WAIT_TIME_TO_CLEAN = 2000;

    ImageButton ibt_back;
    ImageButton ibt_plus;
    ImageView iv_photo;
    EditText et_name;
    EditText et_company;
    EditText et_phone;
    RadioButton rbt_woman;
    RadioButton rbt_man;
    Button bt_add;
    //READ权限请求码
//    final int MY_PERMISSION_REQUEST_READ = 0;
    //剪裁图片的请求码
    final int CROP_REQUEST_CODE = 2;
    //访问服务器请求队列
    RequestQueue requestQueue;
    //工作人员ID
    String regid;
    String name;
    Bitmap bmp_photo;

    Map<String, String> guestInfoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        //初始化控件
        initComponents();
    }

    private void initComponents() {
        //的尺寸适配问题  图片的压缩
        ibt_back = (ImageButton) findViewById(R.id.img_bt_info_back);
        ibt_plus = (ImageButton) findViewById(R.id.img_bt_add_guest_photo);
        iv_photo = (ImageView) findViewById(R.id.iv_guest_photo);
        et_name = (EditText) findViewById(R.id.et_guest_name);
        et_company = (EditText) findViewById(R.id.et_guest_company);
        et_phone = (EditText) findViewById(R.id.et_guest_phone);
        bt_add = (Button) findViewById(R.id.bt_add);
        rbt_man = (RadioButton) findViewById(R.id.rd_bt_man);
        rbt_woman = (RadioButton) findViewById(R.id.rd_bt_woman);

        ibt_back.setOnClickListener(this);
        ibt_plus.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        bt_add.setOnClickListener(this);

        bt_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    bt_add.setTextColor(AddGuestActivity.this.getResources().getColor(R.color.colorBlack));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    bt_add.setTextColor(AddGuestActivity.this.getResources().getColor(R.color.white));
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GuestListUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "当前网络不可用!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bt_info_back:
                //返回上个界面
                this.finish();
                break;
            case R.id.img_bt_add_guest_photo:
            case R.id.iv_guest_photo:
                //调用系统相册
                //运行时权限 todo
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_DENIED) {
//                    //读取sdCard权限未授予  申请权限
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_READ);
//                    return;
//                }
                pickImage();
                break;
            case R.id.bt_add:
                //判断各项信息是否完整
                checkPerInfo();
                break;
        }

    }

    /**
     * 检查各项信息是否填写及符合要求
     */
    private void checkPerInfo() {
        Log.i("AddGuestActivity", "点击了添加");
        name = et_name.getText().toString();
        String company = et_company.getText().toString();
        String phone = et_phone.getText().toString();
        String sex = "女";

        if (!guestInfoMap.containsKey("gphoto")) {
            Toast.makeText(this, "请添加一张照片！", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.equals("")) {
            Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckLegality.isContainSpace(name)) {
            Toast.makeText(this, "姓名不能包含空格！", Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckLegality.isContainSpecialChar(name)) {
            Toast.makeText(this, "姓名不能包含特殊字符！", Toast.LENGTH_SHORT).show();
            return;
        } else if (company.equals("")) {
            Toast.makeText(this, "单位不能为空！", Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckLegality.isContainSpace(company)) {
            Toast.makeText(this, "单位不能包含空格！", Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckLegality.isContainSpecialChar(company)) {
            Toast.makeText(this, "单位不能包含特殊字符！", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.length() < 11 || !CheckLegality.isPhoneValid(phone)) {
            Toast.makeText(this, "手机格式填写不正确！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rbt_woman.isChecked()) {
            sex = "女";
        } else if (rbt_man.isChecked()) {
            sex = "男";
        }
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        regid = sp.getString("eid", null);

        guestInfoMap.put("gname", name);
        guestInfoMap.put("gtel", phone);
        guestInfoMap.put("gcompany", company);
        guestInfoMap.put("gsex", sex);
        guestInfoMap.put("regid", regid);

        //传输信息
        transferGuestInfo();
    }

    /**
     * 传输添加的嘉宾信息
     */
    private void transferGuestInfo() {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, ServerInfo.CREATE_NEW_GUEST_URL,
                new AddGuestResponseListener(), new AddGuestResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return guestInfoMap;
            }
        };
        //访问服务器请求队列
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);
    }

    /**
     *
     */
    private class AddGuestResponseListener implements Response.Listener<String> {

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
                    //添加至我的嘉宾
                    addToMyGuest(regid, name, bmp_photo);
                    //清空输入信息
                    cleanGuestInfo();
                    break;
                case 1:
                    //此嘉宾已存在
                    Toast.makeText(AddGuestActivity.this, "此嘉宾已存在！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddGuestActivity.this, GuestInfoActivity.class);
                    intent.putExtra("gname", name);
                    intent.putExtra("guest_type", 1);
                    startActivity(intent);
                    //清空输入信息
                    cleanGuestInfo();
                    break;
                case 2:
                    //数据错误
                    Toast.makeText(AddGuestActivity.this, "数据传输错误，请再次添加！", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    /**
     * 等一段时间 后清空输入框信息
     */
    private void cleanGuestInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(WAIT_TIME_TO_CLEAN);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void addToMyGuest(final String eid, final String guest_name, final Bitmap guest_photo) {
        //添加至“我的嘉宾”
        StringRequest addRequest = new StringRequest(Request.Method.POST, ServerInfo.ADD_TO_GUEST_LIST_URL,
                new AddMyGuestResponseListener(), new AddGuestResponseErrorListener()) {
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
     *
     */
    private class AddMyGuestResponseListener implements Response.Listener<String> {

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
                    //添加成功
                    Toast.makeText(AddGuestActivity.this, "添加成功！", Toast.LENGTH_LONG).show();
                    //更新内存
                    GuestInfo guestInfo = new GuestInfo(name, bmp_photo);
                    GuestListUtil.addGuest(guestInfo);
                    break;
                case 1:
                    //此嘉宾已存在
                    Toast.makeText(AddGuestActivity.this, "此嘉宾已在我的嘉宾中！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    //数据错误
                    Toast.makeText(AddGuestActivity.this, "数据传输错误，没有添加至我的嘉宾中！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     *
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            et_company.setText("");
            et_phone.setText("");
            et_name.setText("");
            iv_photo.setImageResource(R.drawable.rounded_rectangle_white);
            ibt_plus.setVisibility(View.VISIBLE);
        }
    };

    /**
     *
     */
    private class AddGuestResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Transfer", "收到服务器回复");
            //提示网络连接失败
            Toast.makeText(AddGuestActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从相册中选择照片
     */
    public void pickImage() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(pickImageIntent, PICK_PHOTO_FOR_AVATAR);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO_FOR_AVATAR:
                    //剪裁图片
                    ImageUtil.startPhotoZoom(data.getData(), this, CROP_REQUEST_CODE);
                    break;
                case CROP_REQUEST_CODE://返回剪裁后的图片
                    bmp_photo = ImageUtil.getCropImage(this);
                    if (bmp_photo == null) {
                        return;
                    }
                    iv_photo.setImageBitmap(bmp_photo);
                    ibt_plus.setVisibility(View.INVISIBLE);
                    iv_photo.setBackgroundResource(R.color.gray_light);
                    String str_photo = ImageUtil.convertImage(bmp_photo);
                    guestInfoMap.put("gphoto", str_photo);
                    break;
            }
        }
    }

}
