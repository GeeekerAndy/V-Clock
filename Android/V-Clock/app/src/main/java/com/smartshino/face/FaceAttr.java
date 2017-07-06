package com.smartshino.face;


public class FaceAttr {

    boolean isIncludeFace = false;

    public void setIncludeFace(boolean includeFace) {
        isIncludeFace = includeFace;
    }

    public boolean isIncludeFace() {

        return isIncludeFace;
    }

    /**
     * 头部姿态
     */
    int[] mHeadPosition = new int[3];
    /**
     * 人脸矩形框坐标[x,y,w,h]
     */
    int[] mFaceRect = new int[4];
    /**
     * 人脸49点坐标
     */
    int[] mFacePoints = new int[98];
    /**
     * 人脸置信度
     */
    int[] mScor = {0};
    /**
     * 人脸FaceId
     */
    int[] mFaceId = {0};
    /**
     * 左右眼坐标
     */
    int[] mEyePoints = new int[4];
    /**
     * 眼睛张开程度
     */
    int[] mEyeDegree = {0};
    /**
     * 双眼张开程度
     */
    int[] mEyesDegree = new int[2];
    /**
     * 嘴巴张开程度
     */
    int[] mMouthDegree = {0};
    /**
     * 清晰度
     */
    int[] mClarity = {0};

    public int[] getFaceRect() {
        return mFaceRect;
    }

    public int[] getHeadPosition() {
        return mHeadPosition;
    }

    public int[] getFacePoints() {
        return mFacePoints;
    }

    public int[] getScor() {
        return mScor;
    }

    public int[] getFaceId() {
        return mFaceId;
    }

    public int[] getEyePoints() {
        return mEyePoints;
    }

    public int[] getEyeArrayDegree() {
        return mEyeDegree;
    }

    public int getEyeDegree() {
        return mEyeDegree[0];
    }

    public int[] getEyesArrayDegree() {
        return mEyesDegree;
    }

    public int getLeftDegree() {
        return mEyesDegree[0];
    }

    public int getRightDegree() {
        return mEyesDegree[1];
    }

    public int[] getMouthArrayDegree() {
        return mMouthDegree;
    }

    public int getMouthDegree() {
        return mMouthDegree[0];
    }

    public int[] getClarityArray() {
        return mClarity;
    }

    public int getClarity() {
        return mClarity[0];
    }

    public void setmHeadPosition(int[] mHeadPosition) {
        for (int i = 0; i < mHeadPosition.length; i++) {
            this.mHeadPosition[i] = mHeadPosition[i];
        }
    }

    public void setmFaceRect(int[] mFaceRect) {
        for (int i = 0; i < mFaceRect.length; i++) {
            this.mFaceRect[i] = mFaceRect[i];
        }
    }


    public void setmScor(int[] mScor) {
        for (int i = 0; i < mScor.length; i++) {
            this.mScor[i] = mScor[i];
        }
    }


}
