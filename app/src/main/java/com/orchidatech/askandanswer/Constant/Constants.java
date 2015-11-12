package com.orchidatech.askandanswer.Constant;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Constants {

    public static final String SHARED_PREF_NAME = "AskAndAnswerPref";

    public static class DateConversion {
        final static String MONTHS[] = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        public static String getDate(long milliSeconds)
        {
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
}
