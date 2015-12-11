package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Entity.SocialUser;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.Logic.AppGoogleAuth;
import com.orchidatech.askandanswer.Logic.GCMUtilities;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Animation.ViewAnimation;
import com.orchidatech.askandanswer.View.Interface.OnGCMRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnSocialLoggedListener;
import com.orchidatech.askandanswer.View.Utils.Validator;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class Login extends AppCompatActivity {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";

    ImageView iv_logo;
    TextView tv_signup_now;
    TextView tv_forget_password;
    EditText ed_name;
    EditText ed_password;
    Button btn_login;
    CoordinatorLayout mCoordinatorLayout;

    RelativeLayout btn_fb;
    RelativeLayout btn_gplus;
    private SimpleFacebook mSimpleFacebook;
    private String TAG = Login.class.getSimpleName();
    LinearLayout ll_form;
    public static AppGoogleAuth googleAuth;
    private Validator mValidator;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    Animation logoTranslate;
    private Animation animFade;
    private Animation form_translate;
    GoogleCloudMessaging gcm;
    private LoadingDialog loadingDialog;
    private String registration_id;
    private SocialUser socialUser;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        resizeLogo();
        iv_logo.startAnimation(logoTranslate);
        ll_form.startAnimation(form_translate);

    }

    ///////
    private void login(final String username, final String password) {
        loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.logging));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "logging in");
        if(TextUtils.isEmpty(registration_id))
            registerWithGCM(0);
        else
            sendLoginRequest();
//        startActivity(new Intent(this, TermsActivity.class));

    }

    private void sendLoginRequest() {
        WebServiceFunctions.login(Login.this, username, password, registration_id, System.currentTimeMillis(),
                new com.orchidatech.askandanswer.View.Interface.OnLoginListener() {
                    @Override
                    public void onSuccess(long uid, ArrayList<Long> user_categories) {
                        loadingDialog.dismiss();
                        prefEditor.putLong(GNLConstants.SharedPreference.ID_KEY, uid);
//                        prefEditor.putString(GNLConstants.SharedPreference.PASSWORD_KEY, password);
                        prefEditor.putInt(GNLConstants.SharedPreference.LOGIN_TYPE, Enum.LOGIN_TYPE.DEFAULT.getNumericType());
                        prefEditor.commit();
                        if (user_categories != null && user_categories.size() > 0) {
                            startActivity(new Intent(Login.this, MainScreen.class));
                        } else {
                            startActivity(new Intent(Login.this, TermsActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onFail(String cause) {
                        loadingDialog.dismiss();
                        Crouton.cancelAllCroutons();
                        AppSnackBar.showTopSnackbar(Login.this, cause, Color.RED, Color.WHITE);
                    }
                });

    }

    private void socialLogin() {
         loadingDialog = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.logging));
        loadingDialog.setArguments(args);
        loadingDialog.setCancelable(false);
        loadingDialog.show(getFragmentManager(), "logging in");
     prefEditor.putInt(GNLConstants.SharedPreference.LOGIN_TYPE, socialUser.getNetwork()).commit();
     if(TextUtils.isEmpty(registration_id))
         registerWithGCM(1);
     else
         sendSocialSignupRequest();
 }
    private void sendSocialSignupRequest(){
        WebServiceFunctions.socialLogin(Login.this, socialUser, registration_id,
                new com.orchidatech.askandanswer.View.Interface.OnLoginListener() {
                    @Override
                    public void onSuccess(long uid, ArrayList<Long> user_categories) {
                        loadingDialog.dismiss();
                        prefEditor.putLong(GNLConstants.SharedPreference.ID_KEY, uid);
//                        prefEditor.putString(GNLConstants.SharedPreference.PASSWORD_KEY, password);//store it in webserviceFunctions
                        prefEditor.commit();
                        if (user_categories != null && user_categories.size() > 0) {
                            startActivity(new Intent(Login.this, MainScreen.class));
                        } else {
                            startActivity(new Intent(Login.this, TermsActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onFail(String cause) {
                        loadingDialog.dismiss();
                        Crouton.cancelAllCroutons();
                        int loginType = pref.getInt(GNLConstants.SharedPreference.LOGIN_TYPE, 0);
                        AppSnackBar.showTopSnackbar(Login.this, cause, Color.RED, Color.WHITE);
                        if (loginType == Enum.LOGIN_TYPE.FACEBOOK.getNumericType()) {
                            SimpleFacebook.getInstance(Login.this).logout(new OnLogoutListener() {
                                @Override
                                public void onLogout() {
                                }
                            });
                        } else if (loginType == Enum.LOGIN_TYPE.GOOGLE.getNumericType()) {
                            googleAuth.googlePlusLogout();
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

                        }
                    }
                });

    }
    private void registerWithGCM(final int eventType){

        GCMUtilities gcmUtilities = new GCMUtilities(Login.this, GNLConstants.SENDER_ID, new OnGCMRegisterListener() {


            @Override
            public void OnRegistered(String reg_id) {
                registration_id = reg_id;
                Log.i("reg_id", reg_id);
                prefEditor.putString(GNLConstants.SharedPreference.REG_ID, reg_id).commit();
                if(eventType == 0)
                    sendLoginRequest();
                else
                    sendSocialSignupRequest();
            }

            @Override
            public void onFail() {
                loadingDialog.dismiss();
                AppSnackBar.showTopSnackbar(Login.this, "An Error Occurred, Retry", Color.RED, Color.WHITE);

            }
        });
        gcmUtilities.register();

    }
    private void initializeFields() {
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
        googleAuth = new AppGoogleAuth(this);
        googleAuth.mGoogleApiClient.connect();
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mValidator = Validator.getInstance();
        ll_form = (LinearLayout) this.findViewById(R.id.ll_form);
        ed_name = (EditText) this.findViewById(R.id.ed_name);
        ed_name.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        ed_password.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        logoTranslate = AnimationUtils.loadAnimation(Login.this, R.anim.translate_top_bottom);
        animFade = AnimationUtils.loadAnimation(Login.this, R.anim.fade);
        form_translate = AnimationUtils.loadAnimation(Login.this, R.anim.translate_bottom_top);

        btn_login = (Button) this.findViewById(R.id.btn_login);
        tv_signup_now = (TextView) this.findViewById(R.id.tv_signup_now);
        tv_forget_password = (TextView) this.findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgetPassword.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        btn_fb = (RelativeLayout) this.findViewById(R.id.btn_fb);
        btn_gplus = (RelativeLayout) this.findViewById(R.id.btn_gplus);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                 username = ed_name.getText().toString().toLowerCase().trim();
                 password = ed_password.getText().toString();
                if (verifyInputs(username, password)) {
                    v.startAnimation(animFade);
                    login(username, password);
                }
            }
        });
        tv_signup_now.setText(Html.fromHtml(getResources().getString(R.string.tv_signup_now)));
        tv_signup_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                overridePendingTransition(R.anim.slide_in_up_dialog, R.anim.slide_out_down_dialog);
//                finish();
            }
        });

        ////////
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setBackground(getResources().getDrawable(R.drawable.btn_fb_backgnd_on));
                if (mSimpleFacebook.isLogin()) {
                    getFbProfile();
                } else {
                    mSimpleFacebook.login(new OnLoginListener() {
                        @Override
                        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                            Log.i(TAG, "Logged in");
                            getFbProfile();

                        }

                        @Override
                        public void onCancel() {
                            v.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            v.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
                        }

                        @Override
                        public void onFail(String reason) {
                            v.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
                        }
                    });
                }
            }
        });
        btn_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setBackground(getResources().getDrawable(R.drawable.btn_gplus_backgnd_on));
                if (!googleAuth.mGoogleApiClient.isConnected())
                  googleAuth.googlePlusLogin(new OnSocialLoggedListener() {
                        @Override
                        public void onSuccess(SocialUser user) {
//                            UsersDAO.addUser(new Users(1, null, null, user.getName(), user.getEmail(), "123", user.getAvatarURL(), System.currentTimeMillis(), 1, System.currentTimeMillis(), "0252255", 0, "121223"));
//                            startActivity(new Intent(Login.this, TermsActivity.class));
                            socialUser = user;
                            socialLogin();
                            Toast.makeText(Login.this, user.getEmail() + ", " + user.getFname() + ", " + user.getLname(), Toast.LENGTH_LONG).show();
                        }
                    });
                else
                    btn_gplus.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));

            }
        });


    }

    private void hideSoftKeyboard() {
        View view = Login.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean verifyInputs(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            ViewAnimation.blink(Login.this, ed_name);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Login.this, getString(R.string.BR_LOGIN_001), Color.RED, Color.WHITE);
            return false;
        } else if (!mValidator.isValidEmail(username)) {
            ViewAnimation.blink(Login.this, ed_name);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Login.this, getString(R.string.BR_GNL_002), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(password)) {
            ViewAnimation.blink(Login.this, ed_password);
            Crouton.cancelAllCroutons();
            AppSnackBar.showTopSnackbar(Login.this, getString(R.string.BR_LOGIN_002), Color.RED, Color.WHITE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == googleAuth.RC_SIGN_IN) {
            btn_gplus.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));

            if (resultCode == RESULT_OK) {
                googleAuth.signedInUser = false;
            }
            googleAuth.mIntentInProgress = false;
            if (resultCode != RESULT_CANCELED)
                if (!googleAuth.mGoogleApiClient.isConnecting()) {
                    googleAuth.mGoogleApiClient.connect();
                }
        } else
            mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFbProfile() {

        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.ID)
                .add(Profile.Properties.NAME)
                .add(Profile.Properties.PICTURE)
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .build();
        mSimpleFacebook.getProfile(properties, new OnProfileListener() {
            @Override
            public void onComplete(Profile response) {
                super.onComplete(response);
//                UsersDAO.addUser(new Users(1, response.getFirstName(), response.getLastName(), response.getName(), response.getEmail(), "123", response.getPicture(), System.currentTimeMillis(), 1, System.currentTimeMillis(), "0252255", 0, "121223"));
                // startActivity(new Intent(Login.this, TermsActivity.class));
                btn_fb.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
                socialUser = new SocialUser();
                socialUser.setAvatarURL(response.getPicture());
                socialUser.setEmail(response.getEmail());
                socialUser.setFname(response.getFirstName());
                socialUser.setLname(response.getLastName());
                socialUser.setName(response.getName());
                socialUser.setNetwork(SocialUser.NetworkType.FACEBOOK);
                socialLogin();
                Toast.makeText(Login.this, response.getFirstName() + ", " + response.getLastName() + ", " + response.getEmail() + ", " + response.getPicture(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onException(Throwable throwable) {
                super.onException(throwable);
                Toast.makeText(Login.this, throwable.getMessage(), Toast.LENGTH_LONG).show();

                btn_fb.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
            }

            @Override
            public void onFail(String reason) {
                super.onFail(reason);
                Toast.makeText(Login.this, reason, Toast.LENGTH_LONG).show();
                btn_fb.setBackground(getResources().getDrawable(R.drawable.btn_social_backgnd));
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
    }
}
