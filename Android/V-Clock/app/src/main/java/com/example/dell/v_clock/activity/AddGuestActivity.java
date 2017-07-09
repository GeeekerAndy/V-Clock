package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dell.v_clock.R;

import java.io.IOException;

public class AddGuestActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PICK_PHOTO_FOR_AVATAR = 1;

    ImageButton ibt_back;
    ImageButton ibt_plus;
    ImageView iv_photo;
    EditText et_name;
    EditText et_company;
    EditText et_phone;
    RadioButton rbt_woman;
    RadioButton rbt_man;
    Button bt_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        //初始化控件
        initComponents();
    }

    private void initComponents() {
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

                break;
        }

    }

    public void pickImage() {
        ibt_plus.setVisibility(View.INVISIBLE);
        iv_photo.setBackgroundResource(R.drawable.rounded_rectangle_gray);
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
//                    employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
