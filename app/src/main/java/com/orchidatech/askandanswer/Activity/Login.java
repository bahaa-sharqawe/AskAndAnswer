package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orchidatech.askandanswer.R;

public class Login extends Activity {
        ImageView iv_logo;
        Button btn_login;
        TextView tv_signup_now;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_signup_now = (TextView) this.findViewById(R.id.tv_signup_now);
        tv_signup_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });
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
