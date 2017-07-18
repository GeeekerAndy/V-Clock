package com.example.dell.v_clock.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andy on 7/17/17.
 */

public class CheckLegality {

    /**
     * 检查字符串中是否包含空格
     *
     * @param name 输入的姓名字符串
     * @return true 不包含空格；false 包含空格
     */
    public static boolean isNameContainSpace(String name) {
        if (name.contains(" ") || name.length() < 1) {
            return false;
        } else {
            return true;
        }
    }

    //todo  手机号合法性的检查

    /**
     * 检查字符串中是否包含特殊字符
     *
     * @param info 要检查的字符串
     * @return true 包含特殊字符；false 不包含特殊字符
     */
    public static boolean isContainSpecialChar(String info) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(info);
        return matcher.find();
    }

    /**
     * 判断手机号是否合法
     *
     * @param phone 输入的手机号
     * @return true 手机号合法；false 手机号不合法
     */
    public static boolean isPhoneValid(String phone) {
        String[] phoneHeads = {"13", "14", "15", "17", "18",};
        String temp = phone.substring(0, 2);
        Log.i("CheckLegality", "phoneHead = " + temp);
        for (String str : phoneHeads) {
            if (str.equals(temp)) {
                return true;
            }
        }
        return false;
    }

}
