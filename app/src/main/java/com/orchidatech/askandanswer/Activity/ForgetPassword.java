package com.orchidatech.askandanswer.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Animation.ViewAnimation;
import com.orchidatech.askandanswer.View.Interface.OnForgetPasswordListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import dmax.dialog.SpotsDialog;

public class ForgetPassword extends AppCompatActivity {
    EditText ed_email;
    EditText ed_new_password;
    EditText ed_repassword;
    Button btn_update_password;
    Validator mValidator;
    ImageView iv_logo,img_background;
    ImageView iv_back;
    private Animation animFade;
    private CoordinatorLayout ll_parent;
    private FontManager fontManager;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        img_background = (ImageView) this.findViewById(R.id.img_background);
        resizeLogo();
        fontManager = FontManager.getInstance(getAssets());
        ed_email = (EditText) this.findViewById(R.id.ed_email);
        ed_email.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_new_password = (EditText) this.findViewById(R.id.ed_new_password);
        ed_new_password.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_repassword = (EditText) this.findViewById(R.id.ed_repassword);
        ed_repassword.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);

        ed_email.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_new_password.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_repassword.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        btn_update_password = (Button) this.findViewById(R.id.btn_update_password);
        btn_update_password = (Button) this.findViewById(R.id.btn_update_password);
        btn_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ed_email.getText().toString().trim();
                String password = ed_new_password.getText().toString();
                String repassword = ed_repassword.getText().toString();
                if (verifyInputs(email, password, repassword)) {
                    v.startAnimation(animFade);
                    update_password(email, password);
                }
            }
        });
        btn_update_password.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));
        iv_back = (ImageView) this.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ll_parent = (CoordinatorLayout) this.findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mValidator = Validator.getInstance();
        animFade = AnimationUtils.loadAnimation(ForgetPassword.this, R.anim.fade);

    }

    private void update_password(String email, String password) {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.submitting));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(getFragmentManager(), "submitting");
        dialog = new SpotsDialog(ForgetPassword.this, getString(R.string.submitting), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();

        WebServiceFunctions.forget_password(this, email, password, password, new OnForgetPasswordListener(){

            @Override
            public void success(String message) {
                dialog.dismiss();
                Toast.makeText(ForgetPassword.this, message, Toast.LENGTH_LONG).show();
               onBackPressed();
            }

            @Override
            public void fail(String cause) {
                dialog.dismiss();
                Crouton.cancelAllCroutons();
                AppSnackBar.showTopSnackbar(ForgetPassword.this, cause, Color.RED, Color.WHITE);            }
        });
    }

    private boolean verifyInputs(String email, String password, String repassword) {
        if (TextUtils.isEmpty(email)) {
        ViewAnimation.blink(this, ed_email);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_SIGN_005), Color.RED, Color.WHITE);
        return false;
    } else if (!mValidator.isValidEmail(email)) {
        ViewAnimation.blink(this, ed_email);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_GNL_002), Color.RED, Color.WHITE);
        return false;
    } else if (TextUtils.isEmpty(password)) {
        ViewAnimation.blink(this, ed_new_password);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_SIGN_002), Color.RED, Color.WHITE);
        return false;
    } else if (!mValidator.isValidPassword(password)) {
        ViewAnimation.blink(this, ed_new_password);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_GNL_003), Color.RED, Color.WHITE);
        return false;
    } else if (TextUtils.isEmpty(repassword)) {
        ViewAnimation.blink(this, ed_repassword);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_SIGN_003), Color.RED, Color.WHITE);
        return false;
    } else if (!mValidator.isPasswordsMatched(password, repassword)) {
        ViewAnimation.blink(this, ed_new_password);
        ViewAnimation.blink(this, ed_repassword);
        Crouton.cancelAllCroutons();
        AppSnackBar.showTopSnackbar(this, getString(R.string.BR_SIGN_006), Color.RED, Color.WHITE);
        return false;
    }
    return true;
        
        
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(ForgetPassword.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void hideSoftKeyboard() {
        View view = ForgetPassword.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();

    }
    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int) (screenSize.y * 0.25);
        iv_logo.getLayoutParams().width = (int) (screenSize.y * 0.25);
        img_background.getLayoutParams().height = (int) (screenSize.y * 0.33);

    }


}
