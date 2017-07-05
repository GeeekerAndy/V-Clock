package com.example.dell.v_clock.util;

import android.content.Context;

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
     * @param hyuv420sp 传入的YUV420SP图像
     * @param width     图像宽度
     * @param height    图像高度
     * @param rotate    旋转方向 旋转90度，顺1逆-1，0=不转
     * @return 图像中人脸的属性
     */
    public FaceAttr detectFace(byte[] hyuv420sp, int width, int height, int rotate) {
        int bContinue = isFirst ? 0 : 1;
        //第一次传入 或者 图像宽高发生变化时 要初始化算法库
        if (isFirst || width != mSsDuck.getWidth() || height != mSsDuck.getHeight()) {
            //设置算法处理图像宽高
            mSsDuck.setWidth(width);
            mSsDuck.setHeight(height);
            //初始化算法库
            SsDuck.SsMobiDinit(SsDuck.phEnvSet, width, height, 1, null, SsDuck.hOptCfg);
            //设置算法句柄
            SsDuck.ENV_SET = SsDuck.phEnvSet[0];
            isFirst = false;
        }

        //转换YUV420SP图像，到R-G-B图像
        int length = hyuv420sp.length;
        byte[] hRgb24 = new byte[length];
        SsDuck.YuvToRgb(hyuv420sp, width, height, hRgb24);
        //旋转图像
        int[] hWdHi = {width, height};
        SsDuck.DoRotate(hRgb24, hWdHi, rotate);
        //压入图像
        SsDuck.SsMobiFrame(hRgb24, 0, bContinue, SsDuck.ENV_SET);
        //图像中人脸的信息
        FaceAttr faceAttr = new FaceAttr();
        //检测人脸置信度
        int[] mScor = {0};
        SsDuck.SsMobiIsoGo(SsDuck.TD_SCOR, mScor, 1, 0, SsDuck.ENV_SET);
        faceAttr.setmScor(mScor);
        //人脸姿态
        int[] mHeadPosition = new int[3];
        SsDuck.SsMobiIsoGo(SsDuck.TD_POSE, mHeadPosition, 1, 0, SsDuck.ENV_SET);
        faceAttr.setmHeadPosition(mHeadPosition);
        //矩阵位置
        int[]  mFaceRect = new int[4];
        SsDuck.SsMobiIsoGo(SsDuck.TD_RECT,mFaceRect,1,0,SsDuck.ENV_SET);
        return faceAttr;
    }

    /**
     * 结束任务
     */
    public void exitTask() {
        SsDuck.SsMobiDexit(SsDuck.ENV_SET);
    }
}
