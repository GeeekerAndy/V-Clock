package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UpdatePwdActivity extends AppCompatActivity {

    final String TAG = "UpdatePwdActivity";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_PHOTO_FOR_Update_PWD = 2;
    static final int CROP_REQUEST_CODE = 3;
    static final int CROP_REQUEST_CODE_FROM_Camera = 4;


    ImageView employeeUpdatePicture;
    HashMap<String, String> employeePwdMap;
    File photoFile;
    String mCurrentPhotoPath;
    Uri photoURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employee_pwd);
        Button takeUpdatePhoto = (Button) findViewById(R.id.bt_take_update_photo);
        Button selectUpdatePhoto = (Button) findViewById(R.id.bt_select_update_photo);
        Button completeUpdatePwd = (Button) findViewById(R.id.bt_complete_update_pwd);
        employeeUpdatePicture = (ImageView) findViewById(R.id.iv_employee_update_picture);
        employeePwdMap = new HashMap<>();
        takeUpdatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        selectUpdatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");
                pickImageIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(pickImageIntent, PICK_PHOTO_FOR_Update_PWD);
            }
        });
        completeUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!employeePwdMap.containsKey("ephoto")) {
                    Toast.makeText(UpdatePwdActivity.this, "请添加照片", Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(UpdatePwdActivity.this);
                    StringRequest updatePwdRequest = new StringRequest(Request.Method.POST, ServerInfo.MODIFY_EMPLOYEE_INFO_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "更改照片: 返回值：" + response + "ephoto is:" + employeePwdMap.get("ephoto"));
                                    if (response.length() > 0 && response.charAt(0) == '0') {
                                        Toast.makeText(UpdatePwdActivity.this, "更新密码成功！", Toast.LENGTH_SHORT).show();
                                        try {
                                            onBackPressed();
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d(TAG, response);
                                    } else if (response.length() > 0 && response.charAt(0) == '1') {
                                        Toast.makeText(UpdatePwdActivity.this, "密码相似度过低，不允许更改！", Toast.LENGTH_SHORT).show();
                                    } else if (response.length() > 0 && response.charAt(0) == '2') {
                                        Toast.makeText(UpdatePwdActivity.this, "数据错误！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(UpdatePwdActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UpdatePwdActivity.this, "服务器错误！", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            SharedPreferences sp = UpdatePwdActivity.this.getSharedPreferences("loginInfo", MODE_PRIVATE);
                            String eid = sp.getString("eid", null);
                            employeePwdMap.put("eid", eid);
                            employeePwdMap.put("tip", "ephoto");
                            return employeePwdMap;
                        }
                    };
                    requestQueue.add(updatePwdRequest);
                }
            }
        });
    }

    private void startCropPhoto() {
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.provider", photoFile);
//grant uri with essential permission the first arg is the The packagename you would like to allow to access the Uri.
        this.grantUriPermission("com.android.camera", photoURI,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoURI, "image/*");

//you must setup two line below
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
//        intent.putExtra("return-data", true);
//you must setup this
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, CROP_REQUEST_CODE_FROM_Camera);
    }

    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.example.android.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startCropPhoto();
        }
        if (requestCode == PICK_PHOTO_FOR_Update_PWD && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(UpdatePwdActivity.this, "Oops, 发生错误！", Toast.LENGTH_SHORT).show();
            } else {
//                try {
//                    Uri selectedImage = data.getData();
//                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                    imageBitmap = ImageUtil.getResizedBitmap(imageBitmap, 480, 640);
//                    employeeUpdatePicture.setImageBitmap(imageBitmap);
//                    employeePwdMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                ImageUtil.startPhotoZoom(data.getData(), this, CROP_REQUEST_CODE);
            }
        }
        if (requestCode == CROP_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap bitmap = ImageUtil.getCropImage(this);
            employeeUpdatePicture.setImageBitmap(bitmap);
            employeePwdMap.put("ephoto", ImageUtil.convertImage(bitmap));
        }
        if (requestCode == CROP_REQUEST_CODE_FROM_Camera && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageBitmap = ImageUtil.getResizedBitmap(imageBitmap, 480, 640);
//            employeeUpdatePicture.setImageBitmap(imageBitmap);
//            employeePwdMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
            try {
                Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoURI));
                employeeUpdatePicture.setImageBitmap(imageBitmap);
                employeePwdMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
