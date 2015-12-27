package com.orchidatech.askandanswer.View.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by Bahaa on 26/12/2015.
 */
public class BitmapUtility {
    // Scale and maintain aspect ratio given a desired width
    // BitmapScaler.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    // Scale and maintain aspect ratio given a desired height
    // BitmapScaler.scaleToFitHeight(bitmap, 100);

    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }
    public static Bitmap resizeBitmap(Bitmap b, int maxWidth, int maxHeight){
        Log.i("dimensgfdgdfgfdgd", b.getHeight() + " x " + b.getWidth() + " , maxWidth: " + maxWidth + " maxHeight: "+ maxHeight);
        if(b.getHeight() <= maxHeight && b.getWidth() <= maxWidth){
            return b;
        }else if(b.getHeight() > maxHeight && b.getWidth() > maxWidth){
            float factor = maxWidth / (float) b.getWidth();
            int scaledHeight = (int) (b.getHeight() * factor);
            return Bitmap.createScaledBitmap(b, maxWidth, scaledHeight > maxHeight?maxHeight:scaledHeight, true);
        }else if(b.getWidth() > maxWidth && b.getHeight() <= maxHeight){
            float factor = maxWidth / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, maxWidth, (int) (b.getHeight() * factor), true);
//            return scaleToFitWidth(b, maxWidth);
        }else{
            return Bitmap.createScaledBitmap(b, b.getWidth(), maxHeight, true);
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
