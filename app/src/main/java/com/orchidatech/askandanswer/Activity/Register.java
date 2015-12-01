package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

public class Register extends Activity {
    private String TAG = Register.class.getSimpleName();

    EditText ed_fname;
    EditText ed_lname;
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
        ed_fname = (EditText) this.findViewById(R.id.ed_fname);
        ed_lname = (EditText) this.findViewById(R.id.ed_lname);
        ed_email = (EditText) this.findViewById(R.id.ed_email);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        ed_repassword = (EditText) this.findViewById(R.id.ed_repassword);
        btn_signup = (Button) this.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = ed_fname.getText().toString().trim();
                String lname = ed_lname.getText().toString().trim();
                String email = ed_email.getText().toString().trim();
                String password = ed_password.getText().toString();
                String repassword = ed_repassword.getText().toString();
                if (verifyInputs(fname, lname, email, password, repassword)) {
                    register(fname, lname, email, password);
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
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mValidator = Validator.getInstance();
    }

    private void register(String fname, String lname, String email, final String password) {
        final LoadingDialog loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.registering));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "registering");
        WebServiceFunctions.register(this, fname, lname, email, password, URL.DEFAULT_IMAGE, System.currentTimeMillis(), 0,
                new OnRegisterListener() {
                    @Override
                    public void onSuccess(long uid) {
                            loadingDialog.dismiss();
                        SplashScreen.prefEditor.putLong(GNLConstants.SharedPreference.ID_KEY, uid);
                        SplashScreen.prefEditor.putString(GNLConstants.SharedPreference.PASSWORD_KEY, password);
                        SplashScreen.prefEditor.commit();
                        startActivity(new Intent(Register.this, TermsActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(String cause) {
                            loadingDialog.dismiss();
                        AppSnackBar.show(ll_parent, cause, Color.RED, Color.WHITE);
                    }
                });
    }

    private boolean verifyInputs(String fname, String lname, String email, String password, String repassword) {
        if (TextUtils.isEmpty(fname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_001), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(fname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        }
        if (TextUtils.isEmpty(lname)) {
            AppSnackBar.show(ll_parent, getString(R.string.BR_SIGN_007), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(lname)) {
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
    private void hideSoftKeyboard() {
        View view = Register.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
