package com.orchidatech.askandanswer.Constant;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
    public static void showTopSnackbar(Activity activity, String message, int background, int textColor){
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(2500).build();
// Define custom styles for crouton
        Style style = new Style.Builder()
                .setBackgroundColorValue(background)
                .setGravity(Gravity.CENTER_HORIZONTAL)
                .setConfiguration(croutonConfiguration)
                .setTextColorValue(textColor).build();
// Display notice with custom style and configuration
        Crouton.showText(activity, message, style);
//        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG);
//        snackbar.setActionTextColor(textColor);
//        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(background);
//        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
//        textView.setGravity(Gravity.CENTER_HORIZONTAL);
//
////        textView.setTextColor(Color.YELLOW);
//        snackbar.show();
    }
}
