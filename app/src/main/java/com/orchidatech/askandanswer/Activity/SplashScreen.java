package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends Activity {
    private final String TAG = SplashScreen.class.getSimpleName();
    private final int TIME_OUT = (int) (2.5 * 1000);
    private static final float LOGO_SCALE = 0.30f;

    private Handler mHandler;
    private Intent mIntent;
    ImageView iv_logo;
    public static SharedPreferences pref;
    public static SharedPreferences.Editor prefEditor;
    boolean mFirstTime;
    long mId;
    String mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        resizeLogo();

        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        mFirstTime = pref.getBoolean(GNLConstants.SharedPreference.FIRST_TIME_KEY, true);
        mId = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);//user id if is logged
        mPassword = pref.getString(GNLConstants.SharedPreference.PASSWORD_KEY, null); // password for current user

//        if (mFirstTime) {
//            prefEditor.putBoolean(GNLConstants.SharedPreference.FIRST_TIME_KEY, false).commit();
//        } else {
//            fetchPostsFromServer();//to sync posts with server
//        }
//        updateCategoriesFromServer();//to sync categories with server

        if (mId != -1) {//Someone is logged
            List<User_Categories> categories = User_CategoriesDAO.getAllUserCategories(mId);
            if (categories != null && categories.size() > 0) {//this means that current user selected categories previously
//                fetchPostsFromServer();//to sync posts with server
                mIntent = new Intent(this, MainScreen.class);
            }
            else
                mIntent = new Intent(this, TermsActivity.class);
        } else {
            mIntent = new Intent(this, Login.class);
        }

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(mIntent);
                finish();
            }
        }, TIME_OUT);
    }

    private void updateCategoriesFromServer() {
        WebServiceFunctions.getCategories(this, new OnCategoriesFetchedListener() {
            @Override
            public void onSuccess(ArrayList<Category> categories) {

            }

            @Override
            public void onFail(String cause) {

            }
        });
    }

    private void fetchPostsFromServer() {
//        WebServiceFunctions.getPosts(this);
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int) (screenSize.y * LOGO_SCALE);
        iv_logo.getLayoutParams().width = (int) (screenSize.y * LOGO_SCALE);
    }
}
