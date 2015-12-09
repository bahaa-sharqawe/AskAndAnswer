package com.orchidatech.askandanswer.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.plus.Plus;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.Fragment.AboutUs;
import com.orchidatech.askandanswer.Fragment.MyAnswers;
import com.orchidatech.askandanswer.Fragment.MyAsks;
import com.orchidatech.askandanswer.Fragment.MyFavorites;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.Fragment.SearchAndFavorite;
import com.orchidatech.askandanswer.Fragment.Settings;
import com.orchidatech.askandanswer.Fragment.TermsFragment;
import com.orchidatech.askandanswer.Fragment.Timeline;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.DrawerRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnMainDrawerItemClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainScreen extends AppCompatActivity implements TermsFragment.OnDrawerIconClickListener {
    DrawerLayout mDrawerLayout;
    ArrayList<DrawerItem> items;
    ArrayList<String> itemsTitles;
    TypedArray navMenuIcons;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();
        fillData();
        pref = getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
        ft.commit();
        mFragmentManager.executePendingTransactions();

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
                final Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                clearLocalDB();
                int loginType = pref.getInt(GNLConstants.SharedPreference.LOGIN_TYPE, 0);
                prefEditor.remove(GNLConstants.SharedPreference.ID_KEY);
                prefEditor.remove(GNLConstants.SharedPreference.PASSWORD_KEY);
                prefEditor.remove(GNLConstants.SharedPreference.LOGIN_TYPE).commit();
                if(loginType == Enum.LOGIN_TYPE.FACEBOOK.getNumericType()){
                    SimpleFacebook.getInstance(this).logout(new OnLogoutListener() {
                        @Override
                        public void onLogout() {
                         startActivity(intent);
                            finish();
                        }
                    });
                }else if(loginType == Enum.LOGIN_TYPE.GOOGLE.getNumericType()){
                    Login.googleAuth.googlePlusLogout();
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

                    startActivity(intent);
                    finish();
                }else {
                    startActivity(intent);
                    finish();
                }
                break;
            case 7:
                //settings
                fragment = new Settings();
                break;
            case 8:
                //terms
                fragment = new TermsFragment();
                break;
            case 9:
                //about
                fragment = new AboutUs();
                break;
        }
        if (fragment != null) {
            oldPosition = position;
//            defaultState();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
//           if(oldPosition != position)
            ft.addToBackStack("");
            switch (position){
                case 0:
                case 1:
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;
                case 2:
                    ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                    break;
                case 3:
                case 4:
                case 7:
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;

                case 8:
                case 9:
                case 5:
                    ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                    break;


            }
            ft.replace(R.id.fragment_host, fragment);
            ft.commit();
//            mFragmentManager.executePendingTransactions();
        }


    }

    private void clearLocalDB() {
        PostsDAO.deleteAllPosts();;
        CommentsDAO.deleteAllComments();
        User_ActionsDAO.deleteAllUserActions();
        Post_FavoriteDAO.deleteAllUserPostFavorite();
    }

    private void fillData() {
        itemsTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.drawerItems)));
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        items = new ArrayList<>();
        for (int i = 0; i < itemsTitles.size(); i++)
            items.add(new DrawerItem(itemsTitles.get(i), navMenuIcons.getResourceId(i, -1)));
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

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
