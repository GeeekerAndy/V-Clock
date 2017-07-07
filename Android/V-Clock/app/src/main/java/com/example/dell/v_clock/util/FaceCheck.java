package com.example.dell.v_clock.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.Log;

import com.smartshino.face.FaceAttr;
import com.smartshino.face.SsDuck;

/**
 * Created by 王庆伟 on 2017/7/5.
 * 调用SsDuck 识别照片中的人脸
 */

public class FaceCheck {

    /**
     * 调用libSsDuck.so文件中方法的对象
     */
    private SsDuck mSsDuck;
    //是否是第一次调用
    private boolean isFirst = true;

    private static byte[] bytes = new byte[1];

    private final String TAG = "Face";

    /**
     * 构造方法
     */
    public FaceCheck() {
        mSsDuck = SsDuck.getInstance();
    }

    /**
     * 设置模型数据文件
     *
     * @param context 上下文对象
     */
    public void initAlgorithm(Context context) {
        mSsDuck.init(context);
    }

    /**
     * 初始化算法库 并对传入图片进行人脸检测
     * <p>
     * //     * @param hyuv420sp 传入的YUV420SP图像
     *
     * @param width  图像宽度
     * @param height 图像高度
     * @param rotate 旋转方向 旋转90度，顺1逆-1，0=不转
     * @return 图像中人脸的属性
     */
//    public FaceAttr detectFace(byte[] hyuv420sp, int width, int height, int rotate) {
    public FaceAttr detectFace(Bitmap bitmap, int width, int height, int rotate) {
        //是否继续追踪
        int bContinue = isFirst ? 0 : 1;
        //第一次传入 或者 图像宽高发生变化时 要初始化算法库
        if (isFirst || width != mSsDuck.getWidth() || height != mSsDuck.getHeight()) {
            //设置算法处理图像宽高
            mSsDuck.setWidth(width);
            mSsDuck.setHeight(height);
            Log.i("Face", "width = " + width + "  height = " + height);
            //初始化算法库
            int initResult = SsDuck.SsMobiDinit(SsDuck.phEnvSet, width, height, 1, bytes, SsDuck.hOptCfg);
            //设置算法句柄
            SsDuck.ENV_SET = SsDuck.phEnvSet[0];
            Log.i("Face", "initResult = " + initResult);
            Log.i("Face", "ENV_SET = " + SsDuck.ENV_SET);
            isFirst = false;
        }
        //图像中人脸的信息
        FaceAttr faceAttr = new FaceAttr();
        byte[] hRgb24 = getRgbValuesFromBitmap(bitmap);
        //压入图像
        int faceCount = SsDuck.SsMobiFrame(hRgb24, 0, bContinue, SsDuck.ENV_SET);
//        Log.i(TAG, "faceCount = " + faceCount);
//        Log.i("Face", "压入图像完成");
        if (faceCount > 0) {
            Log.i(TAG, "检测到人脸");
            faceAttr.setIncludeFace(true);
            //TODO  人脸的其他属性
            //检测人脸置信度
//            int[] mScor = {0};
//            int scoreResult = SsDuck.SsMobiIsoGo(SsDuck.TD_SCOR, mScor, 1, 0, SsDuck.ENV_SET);
//            Log.i(TAG, "scoreResult = " + scoreResult);
//            faceAttr.setmScor(mScor);
//            //人脸姿态
//            int[] mHeadPosition = new int[3];
//            int poseResult = SsDuck.SsMobiIsoGo(SsDuck.TD_POSE, mHeadPosition, 1, 0, SsDuck.ENV_SET);
//            Log.i(TAG, "poseResult = " + poseResult);
//            faceAttr.setmHeadPosition(mHeadPosition);
//            //矩阵位置
            int[] mFaceRect = new int[4];
            int rectResult = SsDuck.SsMobiIsoGo(SsDuck.TD_RECT, mFaceRect, 1, 0, SsDuck.ENV_SET);
            Log.i(TAG, "rectResult = " + rectResult);
            faceAttr.setmFaceRect(mFaceRect);
//            Log.i(TAG, "score:" + mScor[0]);
//            Log.i(TAG, "pose:" + mHeadPosition[0] + " " + mHeadPosition[1] + " " + mHeadPosition[2]);
            Log.i(TAG, "rect:" + mFaceRect[0] + " " + mFaceRect[1] + " " + mFaceRect[2] + " " + mFaceRect[3]);
        }
        return faceAttr;
    }

    /**
     * 结束任务
     */
    public void exitTask() {
        SsDuck.SsMobiDexit(SsDuck.ENV_SET);
    }

    private byte[] getRgbValuesFromBitmap(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilter colorFilter = new ColorMatrixColorFilter(
                colorMatrix);
        Bitmap argbBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(argbBitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int componentsPerPixel = 3;
        int totalPixels = width * height;
        int totalBytes = totalPixels * componentsPerPixel;

        byte[] rgbValues = new byte[totalBytes];
        @ColorInt int[] argbPixels = new int[totalPixels];
        argbBitmap.getPixels(argbPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < totalPixels; i++) {
            @ColorInt int argbPixel = argbPixels[i];
            int red = Color.red(argbPixel);
            int green = Color.green(argbPixel);
            int blue = Color.blue(argbPixel);
            rgbValues[i * componentsPerPixel + 0] = (byte) red;
            rgbValues[i * componentsPerPixel + 1] = (byte) green;
            rgbValues[i * componentsPerPixel + 2] = (byte) blue;
        }

        return rgbValues;
    }
}
