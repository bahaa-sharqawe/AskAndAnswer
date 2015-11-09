package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.orchidatech.askandanswer.R;

public class Signup extends Activity {
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        resizeLogo();

    }
    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        iv_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }

}
