package com.orchidatech.askandanswer.Constant;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by Bahaa on 14/11/2015.
 */
public class AppSnackBar {

    public static void show(View view, String message, int background, int textColor) {
        if(view.getParent() == null)
            return;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(textColor);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(background);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    public static void show(View view, String message, int background) {
        if(view.getParent() == null)
            return;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(background);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    public static void show(View view, String message) {
        if(view.getParent() == null)
            return;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }
    public static void showTopSnackbar(View view, String message, int background, int textColor){
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(textColor);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(background);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

//        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
