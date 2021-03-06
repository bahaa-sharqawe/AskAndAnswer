package com.orchidatech.askandanswer.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.View.Interface.OnUserCategoriesFetched;
import com.orchidatech.askandanswer.View.Utils.Validator;
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
    public  SharedPreferences pref;
    public  SharedPreferences.Editor prefEditor;
    boolean mFirstTime;
    long mId;
    String mPassword;
    RelativeLayout rl_parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(getApplicationContext());
        initFresco();
        setContentView(R.layout.activity_splash);
//        getFbEmailyuFromAccounts();
        ImageLoaderConfiguration  config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(GNLConstants.MAX_IMAGE_LOADER_CACH_SIZE)).build();
        ImageLoader.getInstance().init(config);


        PostsDAO.checkRowsCount();
        CommentsDAO.checkRowsCount();

        rl_parent = (RelativeLayout) this.findViewById(R.id.rl_parent);
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        resizeLogo();

        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        mFirstTime = pref.getBoolean(GNLConstants.SharedPreference.FIRST_TIME_KEY, true);
        mId = pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1);//user id if is logged
        mPassword = pref.getString(GNLConstants.SharedPreference.PASSWORD_KEY, null); // password for current user

        if (mId != -1) {//Someone is logged
            List<User_Categories> categories = User_CategoriesDAO.getAllUserCategories(mId);
            if (categories != null && categories.size() > 0) {//this means that current user selected categories previously
                updateCategoriesFromServer();
                updateUserCategories(mId);
                mIntent = new Intent(this, MainScreen.class);
            }
            else {
                prefEditor.remove(GNLConstants.SharedPreference.ID_KEY).commit();
                prefEditor.remove(GNLConstants.SharedPreference.PASSWORD_KEY);
                prefEditor.remove(GNLConstants.SharedPreference.LOGIN_TYPE).commit();
                mIntent = new Intent(this, Login.class);
            }
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

    private void updateUserCategories(long mId) {
        WebServiceFunctions.getUserCategories(this, mId, new OnUserCategoriesFetched(){

            @Override
            public void onSuccess(ArrayList<User_Categories> categories) {
//                AppSnackBar.show(rl_parent, "User Categories updating completed...", getResources().getColor(R.color.colorPrimary), Color.WHITE);

            }

            @Override
            public void onFail(String cause) {
//                AppSnackBar.show(rl_parent, "User Categories updating failed...", Color.RED, Color.WHITE);

            }
        });
    }

    private void updateCategoriesFromServer() {
        WebServiceFunctions.getCategories(this, new OnCategoriesFetchedListener() {
            @Override
            public void onSuccess(ArrayList<Category> categories) {
//                AppSnackBar.show(rl_parent, "Categories updated...", getResources().getColor(R.color.colorPrimary), Color.WHITE);

            }

            @Override
            public void onFail(String cause) {
//                AppSnackBar.show(rl_parent, "Categories updating failed...", Color.RED, Color.WHITE);
            }
        });
    }


    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int) (screenSize.y * LOGO_SCALE);
        iv_logo.getLayoutParams().width = (int) (screenSize.y * LOGO_SCALE);
    }
    private void initFresco() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();

        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }


}
