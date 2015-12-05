package com.orchidatech.askandanswer.View.Animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.orchidatech.askandanswer.R;



public class ViewAnimation {

    public static void clockwise(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.clockwise);
        view.startAnimation(animation);
    }

    public static void zoom(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.myanimation);
        view.startAnimation(animation1);
    }

    public static void fade(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.fade);
        view.startAnimation(animation1);
    }

    public static void blink(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.blink);
        view.startAnimation(animation1);
    }

    public static void bounce(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.bounce);
        view.startAnimation(animation1);
    }

    public static void move(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.move);
        view.startAnimation(animation1);
    }

    public static void slide(Context context, View view) {
        Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.slide);
        view.startAnimation(animation1);
    }

}
