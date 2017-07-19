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
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by andy on 7/4/17.
 */

public class ImageUtil {

    static final String TAG = "ImageUtil";

    public static Bitmap convertImage(String base64Str) throws IllegalArgumentException {

        Bitmap bitmap = null;
        try {
            byte[] decodeBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",") + 1),
                    Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
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
     * 剪裁图片
     *
     * @param data 图片数据
     */
    public static void startPhotoZoom(Uri data, Activity context, int requestCode) {
        Intent intentCrop = new Intent("com.android.camera.action.CROP");

        intentCrop.setDataAndType(data, "image/*");
        Log.i(TAG,"Uri = "+data);
        //设置剪裁
        intentCrop.putExtra("crop", "true");
        //aspectX aspectY  宽高比例
        intentCrop.putExtra("aspectX", 3);
        intentCrop.putExtra("aspectY", 4);
        //outputX outputY  剪裁图片宽高
        intentCrop.putExtra("outputX", 480);
        intentCrop.putExtra("outputY", 640);
        intentCrop.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCrop.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //MIUI 有问题
//        intentCrop.putExtra("return-data", "true");
        //先保存
        //确定保存路径
        long current_time =  System.currentTimeMillis();
        tempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" +current_time+ "_temp.jpg");
        intentCrop.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
        intentCrop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        Log.i(TAG, "准备剪裁");
        context.startActivityForResult(intentCrop, requestCode);
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


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromBase64(String base64Str, int reqWidth, int reqHeight) {

        byte[] data = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
}
