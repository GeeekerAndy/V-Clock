package com.example.dell.v_clock.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.dell.v_clock.util.ImageUtil;

public class SelectPhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_PHOTO_FOR_AVATAR = 2;
    ImageView employeePicture;
    HashMap<String , String> employeeInfoMap = new HashMap<>();
    RequestQueue requestQueue;
    String registerURL = "http://121.250.222.39:8080/V-Clock/servlet/RegisterServlet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        Intent receiveIntent = getIntent();
        employeeInfoMap = (HashMap<String, String>)receiveIntent.getSerializableExtra("employeeInfoHashMap");

        Button takePhoto = (Button)findViewById(R.id.bt_take_photo);
        Button selectPhoto = (Button)findViewById(R.id.bt_select_photo);
        Button completeRegister = (Button)findViewById(R.id.bt_complete_register);
        employeePicture = (ImageView)findViewById(R.id.iv_employee_picture);
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
                if(!employeeInfoMap.containsKey("ephoto")) {
                    Toast.makeText(SelectPhotoActivity.this, "请添加照片", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println(employeeInfoMap);
                    StringRequest registerRequest = new StringRequest(Request.Method.POST, registerURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("0")) {
                                        Toast.makeText(SelectPhotoActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                        Intent toLoginIntent = new Intent(SelectPhotoActivity.this, LoginActivity.class);
                                        toLoginIntent.putExtra("etel", employeeInfoMap.get("etel"));
                                        toLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(toLoginIntent);
                                        Log.d("TAG", response);
                                    } else if(response.equals("1")) {
                                        Toast.makeText(SelectPhotoActivity.this, "工作人员已存在！", Toast.LENGTH_SHORT).show();
                                    } else if(response.equals("2")) {
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
                    }){
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
            employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
        }
        if(requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if(data == null) {
                Toast.makeText(SelectPhotoActivity.this, "Oops, 发生错误！", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    employeePicture.setImageBitmap(imageBitmap);
                    employeeInfoMap.put("ephoto", ImageUtil.convertImage(imageBitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
