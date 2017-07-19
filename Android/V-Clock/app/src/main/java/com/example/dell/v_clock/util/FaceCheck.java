package com.example.dell.v_clock.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
     *
     * @param width  图像宽度
     * @param height 图像高度
     * @return 图像中人脸的属性
     */
    public FaceAttr detectFace(Bitmap bitmap, int width, int height) {
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
        int faceCount = SsDuck.SsMobiFrame(hRgb24, 0, 0, SsDuck.ENV_SET);
        if (faceCount > 0) {
            faceAttr.setIncludeFace(true);
            //矩阵位置
            int[] mFaceRect = faceAttr.getFaceRect();
            if (SsDuck.SsMobiIsoGo(SsDuck.TD_RECT, mFaceRect, 0, 0, SsDuck.ENV_SET) >= 0) {
                faceAttr.setmFaceRect(mFaceRect);
                Log.i(TAG, "rect:" + faceAttr.getFaceRect()[0] + " " + faceAttr.getFaceRect()[1] + " "
                        + faceAttr.getFaceRect()[2] + " " + faceAttr.getFaceRect()[3]);
            }
        }

        return faceAttr;
    }

    /**
     * 绘制人脸矩形框
     *
     * @param context    上下文对象
     * @param parentView 矩形框view的父布局 FrameLayout
     * @param targetView Camera画面实时帧呈现对象SurfaceView
     * @param width      含有人脸的Bitmap的宽度
     * @param height     含有人脸的Bitmap的高度
     * @param faceRect   人脸的位置
     */
    public void drawFaceFrame(Context context, ViewGroup parentView, View targetView,
                              int width, int height, int[] faceRect) {

        //初始化图片尺寸
        int[] mirrorRects = new int[4];
        mirrorRects[0] = width - faceRect[0] - faceRect[2];
//        mirrorRects[0] = faceRect[0];
        mirrorRects[1] = faceRect[1];
        mirrorRects[2] = faceRect[2];
        mirrorRects[3] = faceRect[3];

        float ratioHorizontal = (float) targetView.getWidth() / width;
        float ratioVertical = (float) targetView.getHeight() / height;
        //人脸位置
        float faceStartPX = 0 + mirrorRects[0] * ratioHorizontal;
        float faceStartPY = 0 + mirrorRects[1] * ratioVertical;
        float faceEndPX = faceStartPX + mirrorRects[2] * ratioHorizontal;
        float faceEndPY = faceStartPY + mirrorRects[2] * ratioVertical;

        Log.i(TAG, "SurfaceView width = " + targetView.getWidth() + " height = " + targetView.getHeight());

        Log.i(TAG, "mirrorRects:" + mirrorRects[0] + " " + mirrorRects[1] + " "
                + mirrorRects[2] + " " + mirrorRects[3]);
        //确定人脸的位置todo

        //绘制矩形框
        RectFrameView rectFrameView = new RectFrameView(context, faceStartPX - (faceEndPX - faceStartPX), faceStartPY, faceEndPX, faceEndPY);
        if (parentView.getChildCount() > 2) {
            parentView.removeViewAt(2);
        }
        parentView.addView(rectFrameView);
    }

    /**
     * 矩形框 视图
     */
    private class RectFrameView extends View {
        float left;
        float top;
        float right;
        float bottom;
        RectF rectFrame = new RectF();

        public RectFrameView(Context context, float left, float top, float right, float bottom) {
            super(context);
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //画布设为透明色
            canvas.drawColor(Color.TRANSPARENT);
            Paint paint = new Paint();
            //设置Paint为无锯齿
            paint.setAntiAlias(true);

            //设置Paint的颜色
            paint.setColor(Color.BLUE);
            paint.setAlpha(220);
            paint.setTextSize(14);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);

            rectFrame.set(left, top, right, bottom);

            canvas.drawRect(rectFrame, paint);
        }
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
