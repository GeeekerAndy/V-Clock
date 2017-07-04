package com.example.dell.v_clock.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.dell.v_clock.R;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

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
            //获取实时帧
            camera.setPreviewCallback(this);
            //TODO  获取人脸时 自动对焦  暂时放在这
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    if (b) {
                        //自动对焦成功
                    }
                }
            });

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
     * @param bytes
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }
}
