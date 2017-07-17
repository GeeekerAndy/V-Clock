package com.example.dell.v_clock.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.dell.v_clock.ServerInfo;
import com.example.dell.v_clock.util.ImageUtil;

public class SelectPhotoActivity extends AppCompatActivity {

    final String TAG = "SelectPhotoActivity";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_PHOTO_FOR_AVATAR = 2;
    static final int PHOTO_REQUEST_CODE = 3;
    final int CROP_REQUEST_CODE_FROM_Gallery = 4;
    final int CROP_REQUEST_CODE_FROM_Camera = 5;
    final int MY_PERMISSION_REQUEST_READ = 6;
    final int MY_PERMISSION_REQUEST_CAMERA = 7;
    Uri photoURI;

    String mCurrentPhotoPath;
    ImageView employeePicture;
    HashMap<String, String> employeeInfoMap = new HashMap<>();
    RequestQueue requestQueue;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        Intent receiveIntent = getIntent();
        employeeInfoMap = (HashMap<String, String>) receiveIntent.getSerializableExtra("employeeInfoHashMap");

        Button takePhoto = (Button) findViewById(R.id.bt_take_photo);
        Button selectPhoto = (Button) findViewById(R.id.bt_select_photo);
        Button completeRegister = (Button) findViewById(R.id.bt_complete_register);
        employeePicture = (ImageView) findViewById(R.id.iv_employee_register_picture);
        requestQueue = Volley.newRequestQueue(this);

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
                if (!employeeInfoMap.containsKey("ephoto")) {
                    Toast.makeText(SelectPhotoActivity.this, "请添加照片", Toast.LENGTH_SHORT).show();
                } else {
                    StringRequest registerRequest = new StringRequest(Request.Method.POST, ServerInfo.REGISTER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")) {
                                        Toast.makeText(SelectPhotoActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                        Intent toLoginIntent = new Intent(SelectPhotoActivity.this, LoginActivity.class);
                                        toLoginIntent.putExtra("etel", employeeInfoMap.get("etel"));
                                        toLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(toLoginIntent);
                                        Log.d(TAG, response);
                                    } else if (response.equals("1")) {
                                        Toast.makeText(SelectPhotoActivity.this, "工作人员已存在！", Toast.LENGTH_SHORT).show();
                                    } else if (response.equals("2")) {
                                        Toast.makeText(SelectPhotoActivity.this, "数据错误！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SelectPhotoActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SelectPhotoActivity.this, "服务器错误！", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            //Test connection.
//                            HashMap<String, String> map = new HashMap<>();
//                            map.put("etel", "12345678910");
//                            return map;
                            return employeeInfoMap;
                        }
                    };
                    requestQueue.add(registerRequest);
                }
            }
        });
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

    public void pickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            //读取sdCard权限未授予  申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_READ);
            return;
        }
        modifyEmployeePhoto();
    }

    /**
     * Crop employee photo
     * 修改工作人员照片
     */
    private void modifyEmployeePhoto() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        intentFromGallery.setType("image/*");
        startActivityForResult(intentFromGallery, PHOTO_REQUEST_CODE);
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
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 4);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 640);
//        intent.putExtra("return-data", true);
//you must setup this
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, CROP_REQUEST_CODE_FROM_Camera);
    }


    //    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageBitmap = ImageUtil.getResizedBitmap(imageBitmap, 480, 640);
//            employeePicture.setImageBitmap(imageBitmap);
//            employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
            startCropPhoto();
        }
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            //返回相册选择的图片
            ImageUtil.startPhotoZoom(data.getData(), this, CROP_REQUEST_CODE_FROM_Gallery);
        }
        if (requestCode == CROP_REQUEST_CODE_FROM_Gallery && resultCode == RESULT_OK) {
            Bitmap bitmap = ImageUtil.getCropImage(this);
            employeePicture.setImageBitmap(bitmap);
            employeeInfoMap.put("ephoto", ImageUtil.convertImage(bitmap));
        }
        if (requestCode == CROP_REQUEST_CODE_FROM_Camera && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageBitmap = ImageUtil.getResizedBitmap(imageBitmap, 480, 640);
//            employeePicture.setImageBitmap(imageBitmap);
//            employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
            try {
                Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoURI));
                employeePicture.setImageBitmap(imageBitmap);
                employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "权限不足，无法读取图片！", Toast.LENGTH_SHORT).show();
                    //TODO 跳转到权限设置界面 小米手机在该界面授予权限后会有问题 程序会崩掉
                    Context context = this.getApplicationContext();
                    Uri packageURI = Uri.parse("package:" + context.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                } else {
                    //跳转到相册
                    modifyEmployeePhoto();
                }
                break;
            case MY_PERMISSION_REQUEST_CAMERA:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
        }
    }
}
