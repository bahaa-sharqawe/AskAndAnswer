package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.View.Utils.WebServiceFunctions;
import com.sromku.simple.fb.listeners.OnLoginListener;

public class LoginScreen extends Activity {
        ImageView iv_logo;
        TextView tv_signup_now;
        EditText ed_name;
        EditText ed_password;
        Button btn_login;
        CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        resizeLogo();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginScreen.this, TermsActivity.class));
                String username = ed_name.getText().toString();
                String password = ed_password.getText().toString();
               if(verifyInputs(username, password)){
                    login(username, password);
               }

            }
        });
        tv_signup_now.setText(Html.fromHtml(getResources().getString(R.string.tv_signup_now)));
        tv_signup_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, Signup.class));
            }
        });

    }

    private void login(String username, String password) {
        WebServiceFunctions.login(this, username, password,
                new com.orchidatech.askandanswer.View.Interface.OnLoginListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String cause) {
                AppSnackBar.show(mCoordinatorLayout, cause, Color.RED, Color.WHITE);
            }
        });

    }

    private void initializeFields() {
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ed_name = (EditText) this.findViewById(R.id.ed_name);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        tv_signup_now = (TextView) this.findViewById(R.id.tv_signup_now);
    }

    private boolean verifyInputs(String username, String password) {
        if(TextUtils.isEmpty(username)) {
            AppSnackBar.show(mCoordinatorLayout, getString(R.string.BR_LOGIN_001), Color.RED, Color.WHITE);
            return false;
        } else if(TextUtils.isEmpty(password)) {
            AppSnackBar.show(mCoordinatorLayout, getString(R.string.BR_LOGIN_002), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        iv_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }

}
