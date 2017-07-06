package com.smartshino.face;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author Leslie
 * @date 2017/5/19
 * @description SsDuck 算法人脸识别接口
 * 算法调用流程：
 * 1.先调用 SetDatFile 设置模型数据文件。
 * 2.初始化算法库，获取算法句柄。
 * 3.对视频帧的图，调用 SsMobiFrame 接口压入图像
 * 4.针对想要获取的属性，按照 nIndex 的索引，调用 SsMobiIsoGo 函数，获取刚刚压入图片的相关属性
 * 5.如果涉及多线程调用的话，请为每一个线程分配独立的句柄。
 */
public class SsDuck {

    private static final String TAG = "SsDuck";

    static {
        /* 加载人脸算法库 */
        System.loadLibrary("SsDuck");
    }

    //    private final String TAG = SsDuck.class.getSimpleName();
    private final static String VERSION_CODE = "version_code";
    private final static String VERSION = "version";
    private final static String DAT_NAME = "Duck.dat";

    private static final int DEFAULT_WIDTH = 480;
    private static final int DEFAULT_HEIGHT = 640;

    public static final int DETECT_ALL = 0;
    public static final int DETECT_RECT = 1;

    public static final int ALWAYS_DETECT = 0;

    public static long ENV_SET;

    private volatile static SsDuck instance = null;

    private int mWidth = DEFAULT_WIDTH;
    private int mHeight = DEFAULT_HEIGHT;

    Context mContext;

    private static int sDetectType = DETECT_RECT;

    public static long[] phEnvSet = new long[1];
    public static int[] hOptCfg;

    public static SsDuck getInstance() {
        if (instance == null) {
            instance = new SsDuck();
        }
        return instance;
    }

    private SsDuck() {
    }

    public static void setDetectType(int detectType) {
        sDetectType = detectType;
    }

    public static int getDetectType() {
        return sDetectType;
    }

    public void init(Context context) {
        this.mContext = context;
        loadAssetFile("");
    }

    private void loadAssetFile(String version) {
        String sdmdat = mContext.getFilesDir().getAbsolutePath() + "/" + DAT_NAME;
//        sdmdat = "/data/data/com.example.dell.v_clock/files/Duck.dat";
//        sdmdat = "file:///android_asset/Duck.dat";
        File file = new File(sdmdat);
        int versionCode = getVersionCode();
        if (versionCode > getInt(VERSION_CODE) || !getString(VERSION).equals(version)) {
            if (file.exists()) {
                boolean bool = file.delete();
//                Logs.d(TAG, "删除算法数据库:" + bool);// -18
                Log.d(TAG, "loadAssetFile: 删除算法数据库:" + bool);
            }
            saveString(VERSION, version);
            saveInt(VERSION_CODE, versionCode);
            Log.i(TAG, "版本更替");
        }
        try {
            if (!file.exists()) {
                InputStream is = mContext.getResources().getAssets().open(DAT_NAME);
                FileOutputStream fos = new FileOutputStream(sdmdat);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                Log.i(TAG, "file不存在");
            }
            int loadResult = SsSetDatFile(sdmdat, 0, 0);

            String thisV = SsMobiVersn(0);
            String datV = SsMobiVersn(1);

            Log.d(TAG, "dat路径：" + file.exists() + " " + sdmdat + " " + Environment.getExternalStorageDirectory());
            Log.d(TAG, "库版本:" + thisV + " dat版本：" + datV);
            Log.d(TAG, "加载算法数据库完成:" + loadResult);

            hOptCfg = new int[]{28, 1, 40, 0, 65, sDetectType, ALWAYS_DETECT};
            //下面的部分放到外面执行，因为图片分辨率可能不同，但是上面的操作只需要一次，下面的操作每次分辨率变化都要执行，所以分开了。
            /*int initStatus = SsMobiDinit(phEnvSet, mWidth, mHeight, 1, new byte[1], hOptCfg);
            ENV_SET = phEnvSet[0];
            Logs.d(TAG, "初始化算法:" + initStatus + " 环境句柄：" + phEnvSet[0] + "detectType:" + sDetectType);*/
        } catch (Exception e) {
//            Logs.d(TAG, "加载算法数据库失败，程序异常");
            Log.d(TAG, "加载算法数据库失败，程序异常");
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    private int getVersionCode() {// 获取版本号(内部识别号)
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void saveString(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public void saveInt(String key, Integer value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                TAG, Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * 获取信息字串
     *
     * @param nType 值：0=本库版本，1=模型文件版本
     * @return
     */
    public static native String SsMobiVersn(
            int nType
    );

    /**
     * 数据初始化操作
     *
     * @param szFileName 模型文件的全路径名，为空时，释放模型文件(模型文件名)
     * @param nReserved  保留值，请给 0
     * @param nOptCfg    保留值，请给 0(附加配置值)
     * @return >=0 成功， <0 失败。
     * 说明：IOS 和 Android 版算法在算法初始化之前，必须调用这个接口！在内部，模型数据是全局只读，故在多线程下也只需调用一次。
     * IOS 版算法由于需要绑定 APPID,绑定方法为在初始化之前，调用这个借口，szFileName 传入 appid，同时 nReserved 给非零值。
     */
    public static native int SsSetDatFile(
            String szFileName,
            int nReserved,
            int nOptCfg
    );

    /**
     * 初始启动任务
     *
     * @param phEnvSet 算法上下文句柄地址(环境句柄地址)
     * @param nWd      设备或视频出的图，像素点宽度(目标图宽)
     * @param nHi      设备或视频出的图，像素点高度(目标图高)
     * @param nTcnt    启动类型 0：不执行追踪，对每一帧照片运行监测定位操作，1：追踪，当前帧的结果是基于前几帧得到的。
     * @param hTpls    保留值，请给 NULL(连续多个模板)
     * @param hOptCfg  指向 StV4hInit 结构体，用于初始化。为 NULL 时则用默认参数(配置结构参数)
     * @return >=0 成功， <0 失败
     */
    public static native int SsMobiDinit(
            long[] phEnvSet,
            int nWd, int nHi,
            int nTcnt,
            byte[] hTpls,
            int[] hOptCfg
    );

    /**
     * 完成结束任务
     *
     * @param hEnvSet 算法上下文句柄地址(环境句柄地址)
     * @return >=0 成功， <0 失败
     * 说明：在任务完成后，必须调用此释放，以免内存泄漏。
     */
    public static native int SsMobiDexit(
            long hEnvSet
    );

    /**
     * 生成预览小图
     *
     * @param nType   预览变小类型
     * @param hImg    压入图像数据
     * @param hOut    输出预览小图
     * @param hEnvSet 环境句柄地址
     * @return 人脸暂不支持，返回 -12。
     */
    public static native int SsMobiSmall(
            int nType,
            byte[] hImg,
            byte[] hOut,
            long hEnvSet
    );

    /**
     * 推送压入帧图
     *
     * @param hImg      传入图像数据区的首指针(压入图像数据)
     * @param nRotation 保留值，一般给 0 即可(保留请给 0)
     * @param bContinue 0：重启追踪，相当于清除之前帧的数据，当前帧作为追踪的起始帧。1：继续追踪。(是否继续追踪)
     * @param hEnvSet   算法上下文句柄(环境句柄地址)
     * @return 当前帧跟踪到的人脸个数， < 0 失败
     */
    public static native int SsMobiFrame(
            byte[] hImg,
            int nRotation,
            int bContinue,
            long hEnvSet
    );

    /**
     * 任务状态结果
     *
     * @param nIndex  获取属性的索引(信息结构类型)
     * @param hGoDat  需要进行 IO 的数据区的首指针(返回结构体数据)
     * @param nDatRef 获取当前帧第 nDatRef 个人脸的 nIndex 属性(数据辅助参数)
     * @param nOptCfg 附加的配置值(附加的配置值)
     * @param hEnvSet 算法上下文句柄(环境句柄地址)
     * @return >=0 成功， <0 失败。
     */
    public static native int SsMobiIsoGo(
            int nIndex,
            int[] hGoDat,
            int nDatRef,
            int nOptCfg,
            long hEnvSet
    );

    /**
     * 转换YUV420SP图像，到R-G-B图像(由外部分配提供内存空间)
     */
    public static native int YuvToRgb(
            byte[] h420sp, /* []：输入YUV420SP图像 */
            int nWd, int nHi, /* 图像的宽度和高度 */
            byte[] hRgb24 /* []：输出转换后的RGB图像 */
    );

    /* 旋转90度(顺1逆-1)，-1000左右-1001上下，宽高会变，更新R-G-B图像区 */
    public static native int DoRotate(
            byte[] hRgb24, /* []：待旋转的R-G-B图像 */
            int[] hWdHi, /* [2]：图像的宽度和高度 */
            int nRot /* 旋转90度，顺1逆-1，0=不转 */
    );

    public static final int TD_POSE = 0; // 头部姿态
    public static final int TD_RECT = 1; // 矩形位置
    public static final int TD_LD49 = 2; // 49点坐标
    public static final int TD_SCOR = 3; // 人脸置信度
    public static final int TD_FCID = 4; // 人脸FaceId
    public static final int TD_EYEL = 5; // 左右眼坐标
    public static final int TD_EYED = 6; // 眼睛张开程度
    public static final int TD_TEYD = 7; // 双眼张开程度
    public static final int TD_MOTD = 8; // 嘴巴张开程度
    public static final int TD_LHAR = 9; // 额头刘海占比
    public static final int TD_RSML = 10;// 大笑程度

    public static final int TD_AGEY = 10001; // 年龄
    public static final int TD_GEND = 10002; // 性别
    public static final int TD_OCCL = 10003; // 遮挡物判断
    public static final int TD_SHRP = 10004; // 清晰度
    public static final int TD_LSDP = 10005; // LSD检活
    public static final int TD_SKRT = 10006; // 肤色占比
    public static final int TD_EYEB = 10007; // 眼球旋转角度
    public static final int TD_LIGH = 10008; // 脸部亮度
    public static final int TD_UNFM = 10009; // 脸部均匀程度
}

