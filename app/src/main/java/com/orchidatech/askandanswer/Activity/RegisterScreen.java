package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.View.Utils.WebServiceFunctions;

public class RegisterScreen extends Activity {
    private String TAG = RegisterScreen.class.getSimpleName();

    EditText ed_name;
    EditText ed_email;
    EditText ed_password;
    EditText ed_repassword;
    Button btn_signup;
    Validator mValidator;
    ImageView iv_logo;
    ImageView iv_back;
    LinearLayout ll_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeFields();
        resizeLogo();

    }

    private void initializeFields() {
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        ed_name = (EditText) this.findViewById(R.id.ed_name);
        ed_email = (EditText) this.findViewById(R.id.ed_email);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        ed_repassword = (EditText) this.findViewById(R.id.ed_repassword);
        btn_signup = (Button) this.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ed_name.getText().toString();
                String email = ed_email.getText().toString();
                String password = ed_password.getText().toString();
                String repassword = ed_repassword.getText().toString();
                if (verifyInputs(username, email, password, repassword)) {
                    register(username, email, password);
                }
            }
        });
        iv_back = (ImageView) this.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_parent = (LinearLayout) this.findViewById(R.id.ll_parent);
        mValidator = Validator.getInstance();
    }

    private void register(String username, String email, String password) {
        WebServiceFunctions.register(this, username, email, password, new OnRegisterListener() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(RegisterScreen.this, SelectCategoryScreen.class));
            }

            @Override
            public void onFail(String cause) {
                AppSnackBar.show(ll_parent, cause, Color.RED, Color.WHITE);
            }
        });
    }

    private boolean verifyInputs(String username, String email, String password, String repassword) {
        if (TextUtils.isEmpty(username)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_001), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(username)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(email)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_005), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidEmail(email)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_002), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(password)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_002), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidPassword(password)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_003), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(repassword)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_003), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isPasswordsMatched(password, repassword)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_006), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        iv_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
    }

}
