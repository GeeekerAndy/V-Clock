package com.example.dell.v_clock.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by andy on 7/4/17.
 */

public class ImageUtil {

    public static Bitmap convertImage(String base64Str) throws IllegalArgumentException {
        byte[] decodeBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                        Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
    }

    public static String convertImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
