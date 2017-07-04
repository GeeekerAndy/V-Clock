package com.example.dell.v_clock.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dell.v_clock.R;

import java.io.IOException;
import java.io.InputStream;

public class SelectPhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_PHOTO_FOR_AVATAR = 2;
    ImageView employeePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        Button takePhoto = (Button)findViewById(R.id.bt_take_photo);
        Button selectPhoto = (Button)findViewById(R.id.bt_select_photo);
        Button completeRegister = (Button)findViewById(R.id.bt_complete_register);
        employeePicture = (ImageView)findViewById(R.id.iv_employee_picture);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        completeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void pickImage() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(pickImageIntent, PICK_PHOTO_FOR_AVATAR);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            employeePicture.setImageBitmap(imageBitmap);
        }
        if(requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if(data == null) {
                Toast.makeText(SelectPhotoActivity.this, "Oops, 发生错误！", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    employeePicture.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
