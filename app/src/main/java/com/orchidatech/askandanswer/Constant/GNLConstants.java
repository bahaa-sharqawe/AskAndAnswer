package com.orchidatech.askandanswer.Constant;

import android.content.Context;
import android.content.SharedPreferences;
//
//import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
//import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class GNLConstants {

    public static final int MAX_ROWS_FETCH = 40;

    public final static class Settings_Keys{
        public static String NOTIFICATIONS_SOUND = "notificationAndSound";
    }
    public final static class SharedPreference{
        public static final String SHARED_PREF_NAME = "AskAndAnswerPref";
        public static final String FIRST_TIME_KEY = "FIRST_TIME";
        public static final String ID_KEY = "ID";
        public static final String IS_LOGGED_KEY = "IS_LOGGED";
        public static final String IS_USER_SELECTED_CATEGORIES_KEY = "IS_CATEGORY_SELECTED";
    }
    public final static class DateConversion {
        final static String MONTHS[] = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        public static String getDate(long milliSeconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            String fullDate = MONTHS[calendar.get(Calendar.MONTH)] + " " + buildValueOf(calendar.get(Calendar.DAY_OF_MONTH)) +", " + calendar.get(Calendar.YEAR);
            return fullDate;
        }

        private static String buildValueOf(int value) {
            if (value >= 10)
                return String.valueOf(value);
            else
                return "0" + String.valueOf(value);
        }
    }

    public class Social{
//        public static final int GOOGLE_PLUS_ID = GooglePlusSocialNetwork.ID;
    }

}
