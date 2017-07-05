package com.smartshino.face;

/**
 * Created by 王庆伟 on 2017/7/4.
 */

public class SsDuck {
    static {
        System.loadLibrary("SsDuck");
    }

    /**
     00005ca8 T Java_com_smartshino_face_SsDuck_SsMobiDexit
     00005b50 T Java_com_smartshino_face_SsDuck_SsMobiDinit
     00005d54 T Java_com_smartshino_face_SsDuck_SsMobiFrame
     00005dbc T Java_com_smartshino_face_SsDuck_SsMobiIsoGo
     00005cb0 T Java_com_smartshino_face_SsDuck_SsMobiSmall
     00005a60 T Java_com_smartshino_face_SsDuck_SsMobiVersn
     00005adc T Java_com_smartshino_face_SsDuck_SsSetDatFile
     */

    /**
     * 获取信息字符串
     * @param nType     信息配置类型
     * @param szInfo    配置IO字符串
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobiVersn(int nType, String szInfo);

    /**
     * 初始启动任务
     * @param phEnvSet  环境句柄地址
     * @param nWd       目标图宽
     * @param nHi       目标图高
     * @param nTcnt     0检测，1追踪
     * @param hTpls     连续多个模板
     * @param hOptCfg   配置结构参数
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobiDinit(Void phEnvSet, int nWd, int nHi, int nTcnt, String hTpls, int hOptCfg);

    /**
     * 完成结束任务
     * @param hEnvSet   环境句柄地址
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobiDexit(Void hEnvSet);

    /**
     * 生成预览小图
     * @param nType     预览变小类型
     * @param hImg      压入图像数据
     * @param hOut      输出预览小图
     * @param hEnvSet   环境句柄地址
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobiSmall(int nType, String hImg, String hOut, Void hEnvSet);

    /**
     * 推送压入帧图
     * @param hImg      压入图像数据
     * @param nRotation 保留，请给0
     * @param bContinue 是否继续追踪
     * @param hEnvSet   环境句柄地址
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobiFrame(String hImg, int nRotation, int bContinue, Void hEnvSet);

    /**
     * 任务状态结构
     * @param nIndex    信息结构类型
     * @param hGodat    返回结构体数据
     * @param nDatRef   数据辅助参数
     * @param nOptCfg   附加的配置值
     * @param hEnvSet   环境的句柄地址
     * @return          >=0 成功；<0 失败
     */
    public native int SsMobilsoGo(int nIndex, int hGodat, int nDatRef, int nOptCfg, Void hEnvSet);

    /**
     * 负责数据初始化操作
     * @param szFileName    模型文件名
     * @param nReserved     保留请给0
     * @param nOptCfg       附加配置值
     * @return              >=0 成功；<0 失败
     */
    public native int SsSetDatFile(String szFileName, int nReserved, int nOptCfg);

}
