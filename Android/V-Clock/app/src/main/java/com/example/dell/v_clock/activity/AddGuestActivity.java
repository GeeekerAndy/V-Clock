package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.example.dell.v_clock.util.ImageUtil;

import java.io.IOException;
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

    Map<String, String> guestInfoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        //初始化控件
        initComponents();
    }

    private void initComponents() {
        //TODO iv_photo 的尺寸适配问题

        ibt_back = (ImageButton) findViewById(R.id.img_bt_add_back);
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
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    bt_add.setTextColor(AddGuestActivity.this.getResources().getColor(R.color.white));
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bt_add_back:
                //返回上个界面
                this.finish();
                break;
            case R.id.img_bt_add_guest_photo:
            case R.id.iv_guest_photo:
                //调用系统相册
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
        String name = et_name.getText().toString();
        String company = et_company.getText().toString();
        String phone = et_phone.getText().toString();
        String sex = "女";
        String regid;
        if (!guestInfoMap.containsKey("gphoto")) {
            Toast.makeText(this, "请添加一张照片！", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.equals("") || name.equals(" ")) {
            Toast.makeText(this, "姓名格式填写不正确！", Toast.LENGTH_SHORT).show();
            return;
        } else if (company.equals("") || company.equals(" ")) {
            Toast.makeText(this, "单位格式填写不正确！", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.length() < 11) {
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

        //TODO 传输信息  暂时注释掉 方便测试
        transferGuestInfo();

        //测试
        Log.i("AddGuest", "name = " + name);
        Log.i("AddGuest", "phone = " + phone);
        Log.i("AddGuest", "company = " + company);
        Log.i("AddGuest", "sex = " + sex);
        Log.i("AddGuest", "regid = " + regid);
        Log.i("AddGuest", "photo " + guestInfoMap.get("gphoto"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                    //添加成功
                    Toast.makeText(AddGuestActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    //清空输入信息
                    cleanGuestInfo();
                    break;
                case 1:
                    //此嘉宾已存在
                    Toast.makeText(AddGuestActivity.this, "此嘉宾已存在！", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(AddGuestActivity.this, "Oops, 发生错误！", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    iv_photo.setImageBitmap(imageBitmap);
                    ibt_plus.setVisibility(View.INVISIBLE);
                    iv_photo.setBackgroundResource(R.drawable.rounded_rectangle_gray);
                    guestInfoMap.put("gphoto", ImageUtil.convertImage(imageBitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
