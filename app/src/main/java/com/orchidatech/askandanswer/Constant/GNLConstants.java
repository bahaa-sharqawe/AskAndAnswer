package com.orchidatech.askandanswer.Constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class GNLConstants {

    public static final int POST_LIMIT = 5;
    public static final int MAX_POSTS_ROWS = 100;
    public static final int MAX_COMMENTS_ROWS = 100;
    public static final int COMMENTS_LIMIT = 10;


    public final static class Settings_Keys {
        public static String NOTIFICATIONS_SOUND = "notificationAndSound";
    }

    public final static class SharedPreference {
        public static final String SHARED_PREF_NAME = "AskAndAnswerPref";
        public static final String FIRST_TIME_KEY = "FIRST_TIME";
        public static final String ID_KEY = "ID";
        public static final String PASSWORD_KEY = "PASSWORD";
        public static final String IS_LOGGED_KEY = "IS_LOGGED";
        public static final String IS_USER_SELECTED_CATEGORIES_KEY = "IS_CATEGORY_SELECTED";
        public static final String USER_KEY = "USER";
    }

    public final static class DateConversion {
        final static String MONTHS[] = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        public static String getDate(long milliSeconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            String fullDate = MONTHS[calendar.get(Calendar.MONTH)] + " " + buildValueOf(calendar.get(Calendar.DAY_OF_MONTH)) + ", " + calendar.get(Calendar.YEAR);
            return fullDate;
        }

        private static String buildValueOf(int value) {
            return value >= 10 ? String.valueOf(value) : "0" + String.valueOf(value);
        }
    }

    public static String getStatus(int code){
        String message = "";
        switch (code){
            case 100:
                message = "Connection error";
                break;
            case 200:
                message = "The operation has successfully";
                break;
            case 300:
                message = "The operation failed";
                break;
            case 400:
                message = "Validator fails";
                break;
            case 500:
                message = "Input Error";
                break;
            case 301:
                message = "Couldn't be saved image";
                break;
            case 303:
                message = "User already exist";
                break;
            case 305:
                message = "User does not exist";
                break;
            case 307:
                message = "No search results";
                break;
            case 401:
                message = "Your category doesn't numeric array !";
                break;
            case 501:
                message = "There is no input category !";
                break;
            case 404:
                message = "There is no comment on this post !";
                break;
            case 402:
                message = "There is no post";
                break;
            case 403:
                message = "There is no favorite posts";
                break;
            case 406:
                message = "Can't found Categories for this user";
                break;
            case 408:
                message = "Can't found comment Action";
                break;
            case 505:
                message = "Can't found this comment";
                break;
            case 503:
                message = "Can't found this post with this id";
                break;
            case 504:
                message = "Can't found  User with this id !";
                break;
            case 700:
                message = "Can't add  this search to search history";
                break;
            case 506:
                message = "Can't found  category with this id !";
                break;
            case 900:
                message = "No comments found!";
                break;
            default:
                message = "An error occurred";
                break;
        }
        return message;
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
