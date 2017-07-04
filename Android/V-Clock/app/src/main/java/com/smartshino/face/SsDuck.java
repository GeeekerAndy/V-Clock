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
//    public native int SsMobiDexit(void *hEnvSet);
    public native int SsMobiVersn(int nType,String szInfo);
}
