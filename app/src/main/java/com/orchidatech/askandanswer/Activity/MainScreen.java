package com.orchidatech.askandanswer.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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

import com.orchidatech.askandanswer.Constant.GNLConstants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();
        fillData();
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
                startEvent(position);

//                mDrawerLayout.closeDrawer(rv_navigation);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        rv_navigation.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_navigation.setLayoutManager(llm);
        rv_navigation.setAdapter(adapter);
        startEvent(0);
        mDrawerLayout.closeDrawer(GravityCompat.START);

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

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }

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
                args.putLong(Profile.USER_ID_KEY, SplashScreen.pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1));
                fragment.setArguments(args);
                break;
            case 5:
                //search
                fragment = new SearchAndFavorite();
                break;
            case 6:
                //logout
                SplashScreen.prefEditor.remove(GNLConstants.SharedPreference.ID_KEY);
                SplashScreen.prefEditor.remove(GNLConstants.SharedPreference.PASSWORD_KEY).commit();

                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
            defaultState();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_host, fragment);
//            ft.addToBackStack(null);
            ft.commit();
            mFragmentManager.executePendingTransactions();
            setTitle(position == 0 ? "Questions" : itemsTitles.get(position));
        }


    }

    private void defaultState() {
        getSupportActionBar().show();
        ed_search.setVisibility(View.GONE);
        rl_num_notifications.setVisibility(View.GONE);
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

}
