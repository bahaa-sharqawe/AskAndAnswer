package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.WebServiceFunctions;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.List;

public class LoginScreen extends AppCompatActivity {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";

    ImageView iv_logo;
    TextView tv_signup_now;
    EditText ed_name;
    EditText ed_password;
    Button btn_login;
    CoordinatorLayout mCoordinatorLayout;

    RelativeLayout btn_fb;
    RelativeLayout btn_gplus;
    private SimpleFacebook mSimpleFacebook;
    private String TAG = LoginScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        resizeLogo();

    }
///////
    private void login(String username, String password) {
        startActivity(new Intent(this, TermsActivity.class));
/*        WebServiceFunctions.login(this, username, password,
                new com.orchidatech.askandanswer.View.Interface.OnLoginListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String cause) {
                        AppSnackBar.show(mCoordinatorLayout, cause, Color.RED, Color.WHITE);
                    }
                });*/

    }

    private void initializeFields() {
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ed_name = (EditText) this.findViewById(R.id.ed_name);
        ed_password = (EditText) this.findViewById(R.id.ed_password);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        tv_signup_now = (TextView) this.findViewById(R.id.tv_signup_now);
        btn_fb = (RelativeLayout) this.findViewById(R.id.btn_fb);
        btn_gplus = (RelativeLayout) this.findViewById(R.id.btn_gplus);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ed_name.getText().toString();
                String password = ed_password.getText().toString();
                if (verifyInputs(username, password)) {
                    login(username, password);
                }

            }
        });
        tv_signup_now.setText(Html.fromHtml(getResources().getString(R.string.tv_signup_now)));
        tv_signup_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
            }
        });

        ////////
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSimpleFacebook.login(new OnLoginListener() {
                    @Override
                    public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                        Log.i(TAG, "Logged in");
                        getFbProfile();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }

                    @Override
                    public void onFail(String reason) {

                    }
                });
            }
        });
        btn_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private boolean verifyInputs(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            AppSnackBar.show(mCoordinatorLayout, getString(R.string.BR_LOGIN_001), Color.RED, Color.WHITE);
            return false;
        } else if (TextUtils.isEmpty(password)) {
            AppSnackBar.show(mCoordinatorLayout, getString(R.string.BR_LOGIN_002), Color.RED, Color.WHITE);
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
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void getFbProfile() {
        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.ID)
                .add(Profile.Properties.NAME)
                .add(Profile.Properties.PICTURE)
                .add(Profile.Properties.EMAIL)
                .build();
        mSimpleFacebook.getProfile(properties, new OnProfileListener() {
            @Override
            public void onComplete(Profile response) {
                super.onComplete(response);
                Toast.makeText(LoginScreen.this, response.getName() + ", " + response.getEmail() + ", " + response.getPicture(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }
}
