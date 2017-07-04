package com.example.dell.v_clock.activity;

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

import com.example.dell.v_clock.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    //硬件摄像头对象
    private Camera mCamera;
    //用于实时展示摄像头画面
    private SurfaceView mSurfaceView;
    //与SurfaceView搭配使用
    private SurfaceHolder mSurfaceHolder;
    //后台运行任务 用于处理实时帧数据
    private FaceTask mFaceTask;

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

        //开启扫描线程 识别含有人脸的帧
        new Thread(new ScanThread()).start();

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
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
            //将预览相机内容的横屏转为竖屏
            camera.setDisplayOrientation(270);
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
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if(null != mFaceTask)
        {
            switch (mFaceTask.getStatus())
            {
                case RUNNING:
                    return;
                case PENDING:
                    mFaceTask.cancel(false);
                    break;
            }
        }
        mFaceTask = new FaceTask(bytes);
        mFaceTask.execute((Void)null);
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
            final YuvImage image = new YuvImage(mData, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
            boolean isToJpeg = image.compressToJpeg(new Rect(0, 0, width, height), 100, os);
            if (!isToJpeg) {
                //转换失败 直接返回
                return null;
            }
            byte[] temp = os.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(temp,0,temp.length);
            //旋转图像
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bmp2 = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
            //TODO　进行人脸识别等一系列操作
            //TODO　测试一下数据是否可以显示
            Message msg  = handler.obtainMessage();
            msg.obj = bmp2;
            handler.sendMessage(msg);

            return null;
        }
    }

    private class ScanThread implements Runnable{

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    if(null != mCamera)
                    {
                        //TODO  获取人脸时 自动对焦 这样写是否有效？
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                if (b) {
                                    //自动对焦成功
                                }
                            }
                        });
                        //获取实时帧  调用回调函数 处理实时帧数据
                        mCamera.setOneShotPreviewCallback(CameraActivity.this);
                        Log.i("CameraData","setOneShotPreviewCallback");
                    }
                    //每0.5秒截取一帧
                    Thread.sleep(500);
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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            iv_test.setImageBitmap((Bitmap) msg.obj);
        }
    };

}
