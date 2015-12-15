package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.Logic.GCMUtilities;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Animation.ViewAnimation;
import com.orchidatech.askandanswer.View.Interface.OnGCMRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import dmax.dialog.SpotsDialog;

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
    CoordinatorLayout ll_parent;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    private Animation animFade;
    public String registration_id;
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String repassword;
    private FontManager fontManager;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeFields();
        resizeLogo();

    }

    private void initializeFields() {
        fontManager = FontManager.getInstance(getAssets());
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        ed_fname = (EditText) this.findViewById(R.id.ed_fname);
        ed_fname.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_lname = (EditText) this.findViewById(R.id.ed_lname);
        ed_lname.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_email = (EditText) this.findViewById(R.id.ed_email);
        ed_email.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        ed_password.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);
        ed_repassword = (EditText) this.findViewById(R.id.ed_repassword);
        ed_repassword.getBackground().setColorFilter(getResources().getColor(R.color.ed_underline), PorterDuff.Mode.SRC_ATOP);

        ed_fname.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_lname.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_email.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_password.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));
        ed_repassword.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        btn_signup = (Button) this.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 fname = ed_fname.getText().toString().trim();
                 lname = ed_lname.getText().toString().trim();
                 email = ed_email.getText().toString().trim();
                 password = ed_password.getText().toString();
                 repassword = ed_repassword.getText().toString();
                if (verifyInputs(fname, lname, email, password, repassword)) {
                    v.startAnimation(animFade);
                    register(fname, lname, email, password);
                }
            }
        });
        btn_signup.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));
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
        animFade = AnimationUtils.loadAnimation(Register.this, R.anim.fade);

    }

    private void register(final String fname, final String lname, final String email, final String password) {
//         loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.registering));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(getFragmentManager(), "registering");
        dialog = new SpotsDialog(Register.this, getString(R.string.registering), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();

        if(TextUtils.isEmpty(registration_id))
            registerWithGCM();
        else
            sendRegisterRequest(fname, lname, email, password, registration_id);


    }
    private void registerWithGCM(){

        GCMUtilities gcmUtilities = new GCMUtilities(Register.this, GNLConstants.SENDER_ID, new OnGCMRegisterListener() {


            @Override
            public void OnRegistered(String reg_id) {
                Log.i("regi_id", reg_id);
                registration_id = reg_id;
                prefEditor.putString(GNLConstants.SharedPreference.REG_ID, reg_id).commit();
                sendRegisterRequest(fname, lname, email, password, reg_id);
            }

            @Override
            public void onFail() {
                dialog.dismiss();
                AppSnackBar.showTopSnackbar(Register.this, "An Error Occurred, Retry", Color.RED, Color.WHITE);

            }
        });
        gcmUtilities.register();

    }
    public void sendRegisterRequest(String fname, String lname, String email, final String password, String reg_id){
        WebServiceFunctions.register(Register.this, fname, lname, email, password, URL.DEFAULT_IMAGE, reg_id, System.currentTimeMillis(), 0,
                new OnRegisterListener() {
                    @Override
                    public void onSuccess(long uid) {
                        dialog.dismiss();
                        prefEditor.putLong(GNLConstants.SharedPreference.ID_KEY, uid);
//                        prefEditor.putString(GNLConstants.SharedPreference.PASSWORD_KEY, password);
                        prefEditor.putInt(GNLConstants.SharedPreference.LOGIN_TYPE, com.orchidatech.askandanswer.Constant.Enum.LOGIN_TYPE.DEFAULT.getNumericType());
                        prefEditor.commit();
                        startActivity(new Intent(Register.this, TermsActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(String cause) {
                        dialog.dismiss();
                        Crouton.cancelAllCroutons();
                        AppSnackBar.showTopSnackbar(Register.this, cause, Color.RED, Color.WHITE);
                    }
                });

    }

    private boolean verifyInputs(String fname, String lname, String email, String password, String repassword) {
        if (TextUtils.isEmpty(fname)) {
            ViewAnimation.blink(Register.this, ed_fname);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_001), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(fname)) {
            ViewAnimation.blink(Register.this, ed_fname);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        }
        if (TextUtils.isEmpty(lname)) {
            ViewAnimation.blink(Register.this, ed_lname);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_007), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidUserName(lname)) {
            ViewAnimation.blink(Register.this, ed_lname);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_GNL_004), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(email)) {
            ViewAnimation.blink(Register.this, ed_email);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_005), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidEmail(email)) {
            ViewAnimation.blink(Register.this, ed_email);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_GNL_002), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(password)) {
            ViewAnimation.blink(Register.this, ed_password);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_002), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidPassword(password)) {
            ViewAnimation.blink(Register.this, ed_password);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_GNL_003), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(repassword)) {
            ViewAnimation.blink(Register.this, ed_repassword);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_003), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isPasswordsMatched(password, repassword)) {
            ViewAnimation.blink(Register.this, ed_password);
            ViewAnimation.blink(Register.this, ed_repassword);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Register.this, getString(R.string.BR_SIGN_006), Color.RED, Color.WHITE);
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();

    }
}
