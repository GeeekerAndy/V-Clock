package com.example.dell.v_clock.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
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
import com.example.dell.v_clock.util.FaceCheck;
import com.example.dell.v_clock.util.ImageUtil;
import com.smartshino.face.FaceAttr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    //硬件摄像头对象
    private Camera mCamera;
    //用于实时展示摄像头画面
    private SurfaceView mSurfaceView;
    //与SurfaceView搭配使用
    private SurfaceHolder mSurfaceHolder;
    //后台运行任务 用于处理实时帧数据
    private FaceTask mFaceTask;
    //人脸检测算法调用对象
    private FaceCheck faceCheck;
    //
    private String TAG = "CameraAct";
    //手机号与人脸图片匹配是否成功
    private boolean isMatch = false;
    //是否在等待匹配结果
    private boolean isWaited = false;
    //登录访问URL
    private final String LOGIN_URL = "http://121.250.222.39:8080/V-Clock/servlet/LoginServlet";
    //访问服务器请求队列
    private RequestQueue requestQueue;
    //用户登录手机号
    private String phoneNum;
    //超时毫秒数
    private final int OVERTIME = 10000;
    //捕捉画面的时间间隔
    private final int INTERVAL = 100;
    //捕捉画面的次数
    private int captureCount = 0;

    //测试使用的ImageView
    ImageView iv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        //拍照过程屏幕处于高亮
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置手机屏幕朝向为 竖直
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //初始化人脸识别对象
        faceCheck = new FaceCheck();

        //获取登录界面 用户输入的手机号
        phoneNum = getIntent().getStringExtra("etel");
        Log.i("CameraActivity", "phone = " + phoneNum);
        //初始化请求队列
        requestQueue = Volley.newRequestQueue(this);

        //测试
        iv_test = (ImageView) findViewById(R.id.iv_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mCamera != null && mSurfaceHolder != null) {
                setStartPreview(mCamera, mSurfaceHolder);
            }
        }
        //初始化人脸识别算法
        faceCheck.initAlgorithm(this);
        //开启扫描线程 识别含有人脸的帧
        new Thread(new ScanThread()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        faceCheck.exitTask();
    }

    /**
     * 获取Camera对象
     *
     * @return
     */
    private Camera getCamera() {
        Camera camera = null;
        //默认打开前置摄像头
        Camera.CameraInfo info = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    //打开摄像头 获取Camera对象
                    camera = Camera.open(i);
                } catch (Exception e) {
                    camera = null;
                    e.printStackTrace();
                }
            }
        }
        return camera;
    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //为相机设置参数
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(640, 480);
            mCamera.setParameters(parameters);
            //将预览相机内容的横屏转为竖屏
            //TODO 小米手机翻转270   三星、oneplus：90
            camera.setDisplayOrientation(90);
//            camera.setPreviewCallback(this);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放Camera占用的资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /**
     * SurfaceView的相关方法
     */

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setStartPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.stopPreview();
        setStartPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    /**
     * Camera.PreviewCallback的相关方法 获得实时帧数据 并进行处理
     *
     * @param bytes  实时预览帧视频
     * @param camera
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, Camera camera) {
        if (null != mFaceTask) {
            switch (mFaceTask.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    mFaceTask.cancel(false);
                    break;
            }
        }
//        new Thread(new Runnable() {
//            byte[] mData = bytes;
//            @Override
//            public void run() {
//                Camera.Size size = mCamera.getParameters().getPreviewSize();
//                final int width = size.width;
//                final int height = size.height;
//                //用于预览
//                final YuvImage image = new YuvImage(mData, ImageFormat.NV21, width, height, null);
//                ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
//                //
//                boolean isToJpeg = image.compressToJpeg(new Rect(0, 0, width, height), 100, os);
//                if (!isToJpeg) {
//                    //转换失败 直接返回
//                    return;
//                }
//                byte[] temp = os.toByteArray();
//                Bitmap bmp = BitmapFactory.decodeByteArray(temp, 0, temp.length);
//                //旋转图像
//                Matrix matrix = new Matrix();
//                //小米手机翻转90   三星、oneplus：270
//                matrix.postRotate(90);
//                Bitmap bmp_rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//                //测试一下数据是否可以显示
//                Message msg = handler.obtainMessage();
//                msg.obj = bmp_rotated;
//                handler.sendMessage(msg);
//
//                //进行人脸识别等一系列操作
//                //进行人脸检测
//                FaceAttr faceAttr = faceCheck.detectFace(mData, width, height, 1);
////            Log.i(TAG, "score:" + faceAttr.getScor()[0]);
////            Log.i(TAG, "pose:" + faceAttr.getHeadPosition()[0] + " " + faceAttr.getHeadPosition()[1] + " " + faceAttr.getHeadPosition()[2]);
////            Log.i(TAG, "rect:" + faceAttr.getFaceRect()[0] + " " + faceAttr.getFaceRect()[1] + " " + faceAttr.getFaceRect()[2] + " " + faceAttr.getFaceRect()[3]);
//                faceCheck.exitTask();
//            }
//        }).start();
        mFaceTask = new FaceTask(bytes);
        mFaceTask.execute((Void) null);
    }

    /**
     * 自定义FaceTask类，开启一个线程 分析实时帧数据
     */
    private class FaceTask extends AsyncTask<Void, Void, Void> {

        private byte[] mData;

        //构造函数
        FaceTask(byte[] data) {
            this.mData = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            final int width = size.width;
            final int height = size.height;
            //用于预览
            final YuvImage image = new YuvImage(mData, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
            //
            boolean isToJpeg = image.compressToJpeg(new Rect(0, 0, width, height), 100, os);
            if (!isToJpeg) {
                //转换失败 直接返回
                return null;
            }
            byte[] temp = os.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            //旋转图像
            Matrix matrix = new Matrix();
            //TODO 小米手机翻转90   三星、oneplus：270
            matrix.postRotate(270);
            Bitmap bmp_rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            //TODO　进行人脸识别等一系列操作
            //进行人脸检测
            FaceAttr faceAttr = faceCheck.detectFace(bmp_rotated, width, height, 1);
            //测试一下数据是否可以显示
            if (faceAttr.isIncludeFace()) {
                Message msg = handler.obtainMessage();
                msg.obj = bmp_rotated;
                handler.sendMessage(msg);
            }
            //传输图片与用户手机号
            if (!isMatch && !isWaited && faceAttr.isIncludeFace()) {//只有手机号与人脸还没匹配 并且 此时没有在等待服务器回应时，才会发送数据
                isWaited = true;
                transferPhoneImg(bmp_rotated);
            }
            return null;
        }
    }


    private class ScanThread implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (null != mCamera) {
                        //获取人脸时 自动对焦 这样写是否有效？
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                if (b) {
                                    //自动对焦成功
                                    //获取实时帧  调用回调函数 处理实时帧数据
                                    mCamera.setOneShotPreviewCallback(CameraActivity.this);
//                                    Log.i("CameraData", "setOneShotPreviewCallback");
                                }
                            }
                        });
                    }
                    //每0.1秒截取一帧
                    Thread.sleep(INTERVAL);
                    if (++captureCount >= OVERTIME / INTERVAL) {
                        //TODO 超时检测 发送超时Message

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 测试代码  将截取的实时帧显示在一个ImageView中
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iv_test.setImageBitmap((Bitmap) msg.obj);
        }
    };

    /**
     * 将用户手机号与捕捉到的图片发送到后台服务器
     *
     * @param bmp_rotated
     */
    private void transferPhoneImg(final Bitmap bmp_rotated) {

        StringRequest loginRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new LoginResponseListener(), new LoginResponseErrorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("etel", phoneNum);
                Log.i("CameraActivity", "phoneTo = " + phoneNum);
                String imgStr = ImageUtil.convertImage(bmp_rotated);
                map.put("ephoto", imgStr);
                return map;
            }
        };
        requestQueue.add(loginRequest);
    }

    private class LoginResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {

            //收到服务器回复 不再等待回复
            isWaited = false;

            int lengthOfResponse = response.length();
            int intOfResponse = 0;
            try {
                intOfResponse = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                //返回数据包含非数字信息
                Log.i("CameraActivity", "response 包含非数字信息");
                e.printStackTrace();
            }
            if (lengthOfResponse == 1) {
                switch (intOfResponse) {
                    case 1://无此工作人员
                        //提示手机号位注册
                        Toast.makeText(CameraActivity.this, "该手机号未注册", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CameraActivity.this, LoginActivity.class);
                        startActivity(intent);
                        CameraActivity.this.finish();
                        break;
                    case 2:
                        //数据错误  登录人脸不匹配
                        isMatch = false;
                        break;
                }
            } else if (lengthOfResponse == 4 && intOfResponse >= 0) {
                //跳转到主界面 传入eid
                //提示匹配成功信息
                Toast.makeText(CameraActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                isMatch = true;
                //TODO 保存登录信息 只要不注销账号 下次不做登录验证

                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                intent.putExtra("eid", response);
                Log.i("CameraActivity", "eid = " + response);
                startActivity(intent);
                CameraActivity.this.finish();
            }
        }
    }

    private class LoginResponseErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            //提示网络连接失败
            isWaited = false;
            Toast.makeText(CameraActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
    }
}
