package com.example.dell.v_clock.object;

import android.graphics.Bitmap;

/**
 * Created by 王庆伟 on 2017/7/10.
 */

public class GuestInfo {

    private String guestName = null;
    private String guestSex = null;
    private String guestCompany = null;
    private String guestPhone = null;
//    private String guestBasePhoto = null;
    private Bitmap guestBitmapPhoto = null;

    private boolean isMyGuest = false;

    public GuestInfo(String guestName) {
        this.guestName = guestName;
    }

//    public GuestInfo(String guestName, String guestBasePhoto) {
//        this.guestName = guestName;
//        this.guestBasePhoto = guestBasePhoto;
//    }

    public GuestInfo(String guestName, Bitmap guestBitmapPhoto) {
        this.guestName = guestName;
        this.guestBitmapPhoto = guestBitmapPhoto;
    }

//    public GuestInfo(String guestName, String guestSex, String guestCompany, String guestPhone, String guestBasePhoto) {
//        this.guestName = guestName;
//        this.guestSex = guestSex;
//        this.guestCompany = guestCompany;
//        this.guestPhone = guestPhone;
//        this.guestBasePhoto = guestBasePhoto;
//    }

    public GuestInfo(String guestName, String guestSex, String guestCompany, String guestPhone, Bitmap guestBitmapPhoto) {
        this.guestName = guestName;
        this.guestSex = guestSex;
        this.guestCompany = guestCompany;
        this.guestPhone = guestPhone;
        this.guestBitmapPhoto = guestBitmapPhoto;
    }

    public boolean isMyGuest() {
        return isMyGuest;
    }

    public void setMyGuest(boolean myGuest) {
        isMyGuest = myGuest;
    }
    public String getGuestName() {
        return guestName;
    }

    public String getGuestSex() {
        return guestSex;
    }

    public String getGuestCompany() {
        return guestCompany;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

//    public String getGuestBasePhoto() {
//        return guestBasePhoto;
//    }

    public Bitmap getGuestBitmapPhoto() {
        return guestBitmapPhoto;
    }

    public void setGuestSex(String guestSex) {
        this.guestSex = guestSex;
    }

    public void setGuestCompany(String guestCompany) {
        this.guestCompany = guestCompany;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

//    public void setGuestBasePhoto(String guestBasePhoto) {
//        this.guestBasePhoto = guestBasePhoto;
//    }

    public void setGuestBitmapPhoto(Bitmap guestBitmapPhoto) {
        this.guestBitmapPhoto = guestBitmapPhoto;
    }
}
