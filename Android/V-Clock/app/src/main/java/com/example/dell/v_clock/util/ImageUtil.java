package com.example.dell.v_clock.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

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

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    //图片暂时保存的路径
    static Uri tempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "temp.jpg");

    /**
     * 剪裁鱼片
     *
     * @param data 图片数据
     */
    public static void startPhotoZoom(Uri data, Activity context, int requestCode) {
        Intent intentCrop = new Intent("com.android.camera.action.CROP");
        intentCrop.setDataAndType(data, "image/*");
        //设置剪裁
        intentCrop.putExtra("crop", "true");
        //aspectX aspectY  宽高比例
        intentCrop.putExtra("aspectX", 3);
        intentCrop.putExtra("aspectY", 4);
        //outputX outputY  剪裁图片宽高
        intentCrop.putExtra("outputX", 480);
        intentCrop.putExtra("outputY", 640);
        //MIUI 有问题
//        intentCrop.putExtra("return-data", "true");
        //先保存

//        Log.i("GuestInfoActiviyu", "tempFile: " + tempFile);
        intentCrop.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
        intentCrop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        context.startActivityForResult(intentCrop, requestCode);
        return;
    }

    /**
     * 获取剪裁后的图片
     *
     * @param context 上下文对象
     * @return Bitmap对象
     */
    public static Bitmap getCropImage(Context context) {
        Bitmap bmp_photo = null;
        try {
            bmp_photo = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp_photo;
    }

}
