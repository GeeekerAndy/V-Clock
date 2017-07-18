package com.example.dell.v_clock.util;

/**
 * Created by andy on 7/17/17.
 */

public class CheckLegality {

    public static boolean isNameContainSpace(String name) {
        if(name.contains(" ") || name.length() < 1) {
            return false;
        } else {
            return true;
        }
    }
}
