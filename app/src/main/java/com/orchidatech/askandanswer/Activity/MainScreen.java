package com.orchidatech.askandanswer.Activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.NotificationsDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.Fragment.AboutUs;
import com.orchidatech.askandanswer.Fragment.Feedback;
import com.orchidatech.askandanswer.Fragment.LoadingDialog;
import com.orchidatech.askandanswer.Fragment.MyAnswers;
import com.orchidatech.askandanswer.Fragment.MyAsks;
import com.orchidatech.askandanswer.Fragment.MyFavorites;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.Fragment.SearchAndFavorite;
import com.orchidatech.askandanswer.Fragment.Settings;
import com.orchidatech.askandanswer.Fragment.TermsFragment;
import com.orchidatech.askandanswer.Fragment.Timeline;
import com.orchidatech.askandanswer.Logic.AppGoogleAuth;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.DrawerRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnLogoutlistener;
import com.orchidatech.askandanswer.View.Interface.OnMainDrawerItemClickListener;
import com.orchidatech.askandanswer.WebService.WebServiceFunctions;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.util.ArrayList;
import java.util.Arrays;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import dmax.dialog.SpotsDialog;

public class MainScreen extends AppCompatActivity implements TermsFragment.OnDrawerIconClickListener {
    DrawerLayout mDrawerLayout;
    ArrayList<DrawerItem> items;
    ArrayList<String> itemsTitles;
    TypedArray navMenuIcons;
    private TypedArray navMenuIcons_on;
    DrawerRecViewAdapter adapter;
    RecyclerView rv_navigation;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    MaterialEditText ed_search;
    RelativeLayout rl_num_notifications;
    FragmentManager mFragmentManager;
    public static int oldPosition = -1;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    public  static AppGoogleAuth googleAuth;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFresco();
//        Fresco.initialize(getApplicationContext());
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1) == -1){
            final Intent intent = new Intent(MainScreen.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        setCustomActionBar();
        fillData();
        if(Login.googleAuth == null){
            googleAuth = new AppGoogleAuth(this);
            googleAuth.mGoogleApiClient.connect();
        }

        prefEditor = pref.edit();
        mFragmentManager = getFragmentManager();
        ed_search = (MaterialEditText) findViewById(R.id.ed_search);
        rl_num_notifications = (RelativeLayout) findViewById(R.id.rl_num_notifications);

        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) { // null => toolbar

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        rv_navigation = (RecyclerView) this.findViewById(R.id.rv_navigation);
        adapter = new DrawerRecViewAdapter(this, items, new OnMainDrawerItemClickListener() {
            @Override
            public void onClick(int position) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startEvent(position);

//                mDrawerLayout.closeDrawer(rv_navigation);
            }
        });
        rv_navigation.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_navigation.setLayoutManager(llm);
        rv_navigation.setAdapter(adapter);
//        startEvent(0);
        mDrawerLayout.closeDrawer(GravityCompat.START);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fragment_host, new Timeline());
        ft.addToBackStack("0");
        ft.commit();
        mFragmentManager.executePendingTransactions();
//        startEvent(0);

//        mDrawerLayout.openDrawer(rv_navigation);
    }

    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    public void startEvent(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                //timeline
                fragment = new Timeline();
                break;
            case 1:
                //my asks
                fragment = new MyAsks();
                break;

            case 2:
                //my answers
                fragment = new MyAnswers();
                break;

            case 3:
                //my favorite
                fragment = new MyFavorites();
                break;

            case 4:
                //profile
                fragment = new Profile();
                Bundle args = new Bundle();
                args.putLong(Profile.USER_ID_KEY, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1));
                fragment.setArguments(args);
                break;
            case 5:
                //search
                startActivity(new Intent(MainScreen.this, Search.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);

//                fragment = new SearchAndFavorite();
                break;
            case 6:
                //logout
                logout();
                break;
//            case 7:
//                //settings
//                fragment = new Settings();
//                break;
            case 7:
                //terms
                fragment = new TermsFragment();
                break;
            case 8:
                //about
                fragment = new AboutUs();
                break;
            case 9:
                //about
                fragment = new Feedback();
                break;
        }
        if (fragment != null) {
            oldPosition = position;
//            defaultState();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
//           if(oldPosition != position)
            ft.addToBackStack(position + "");
            switch (position){
                case 0:
                case 1:
                case 9:
                    ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                    break;
                case 3:
                case 7:
                case 2:
                    ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                    break;
                case 4:
                    ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down);
                    break;
                case 8:
                case 5:

                    ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                    break;

            }
            ft.replace(R.id.fragment_host, fragment);
            ft.commit();
            mFragmentManager.executePendingTransactions();
            updateDrawer(position);
////            for(int i = 0; i < items.size(); i++){
////                items.get(i).setIsSelected(i==position?true:false);
////            }
//            adapter.notifyDataSetChanged();

        }


    }

    private void logout() {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        Bundle args = new Bundle();
//        args.putString(LoadingDialog.DIALOG_TEXT_KEY, getString(R.string.logging_out));
//        loadingDialog.setArguments(args);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show(getFragmentManager(), "logging out");
        dialog = new SpotsDialog(MainScreen.this, getString(R.string.logging_out), R.style.SpotsDialogCustom);
        dialog.setCancelable(false);
        dialog.show();
        WebServiceFunctions.logout(MainScreen.this, pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1), new OnLogoutlistener(){

            @Override
            public void onSuccess() {
                dialog.dismiss();
                final Intent intent = new Intent(MainScreen.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                clearLocalDB();
                int loginType = pref.getInt(GNLConstants.SharedPreference.LOGIN_TYPE, 0);
                prefEditor.remove(GNLConstants.SharedPreference.ID_KEY);
                prefEditor.remove(GNLConstants.SharedPreference.LOGIN_TYPE);
                prefEditor.remove(GNLConstants.SharedPreference.REG_ID).commit();
                if(loginType == Enum.LOGIN_TYPE.FACEBOOK.getNumericType()){
                    FacebookSdk.sdkInitialize(MainScreen.this);
                    LoginManager.getInstance().logOut();
                    startActivity(intent);
                    finish();
//                    SimpleFacebook.getInstance(MainScreen.this).logout(new OnLogoutListener() {
//                        @Override
//                        public void onLogout() {
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
                }else if(loginType == Enum.LOGIN_TYPE.GOOGLE.getNumericType()){
                    if(Login.googleAuth != null)
                        Login.googleAuth.googlePlusLogout();
                    else
                        googleAuth.googlePlusLogout();
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

                    startActivity(intent);
                    finish();
                }else {
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFail(String cause) {
                    dialog.dismiss();
                Crouton.cancelAllCroutons();
                AppSnackBar.showTopSnackbar(MainScreen.this, cause, Color.RED, Color.WHITE);
            }
        });
    }

    public static void clearLocalDB() {
        PostsDAO.deleteAllPosts();;
        CommentsDAO.deleteAllComments();
        User_ActionsDAO.deleteAllUserActions();
        Post_FavoriteDAO.deleteAllUserPostFavorite();
        NotificationsDAO.deleteAllNotifications();
    }

    private void fillData() {
        itemsTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.drawerItems)));
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navMenuIcons_on = getResources().obtainTypedArray(R.array.nav_drawer_icons_on);
        items = new ArrayList<>();
        for (int i = 0; i < itemsTitles.size(); i++)
            items.add(new DrawerItem(itemsTitles.get(i), navMenuIcons.getResourceId(i, -1), navMenuIcons_on.getResourceId(i, -1), i==0?true:false));
        navMenuIcons.recycle();
    }

    @Override
    public void onClick() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);

        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
            Log.i("cxczc", getFragmentManager().getBackStackEntryCount()+"");
            if(getFragmentManager().getBackStackEntryCount() > 1) {
                FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 2);
                int position = Integer.parseInt(backEntry.getName());
                updateDrawer(position);
            }
        } else {
            super.onBackPressed();
        }
    }

    public void updateDrawer(int position) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setIsSelected(i == position ? true : false);
            Log.i("xcxc", position + "");
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == googleAuth.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                googleAuth.signedInUser = false;
            }
            googleAuth.mIntentInProgress = false;
            if (resultCode != RESULT_CANCELED)
                if (!googleAuth.mGoogleApiClient.isConnecting()) {
                    googleAuth.mGoogleApiClient.connect();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
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
