package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
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
import android.view.View;
import android.widget.RelativeLayout;

import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.Fragment.AboutUs;
import com.orchidatech.askandanswer.Fragment.MyPosts;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.Fragment.SearchAndFavorite;
import com.orchidatech.askandanswer.Fragment.Settings;
import com.orchidatech.askandanswer.Fragment.TermsFragment;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.DrawerRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnDrawerItemClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TermsFragment.OnDrawerIconClickListener {
    DrawerLayout mDrawerLayout;
    ArrayList<DrawerItem> items;
    ArrayList<String> itemsTitles;
    private TypedArray navMenuIcons;
    DrawerRecViewAdapter adapter;
    RecyclerView rv_navigation;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    int oldPosition = -1;
    MaterialEditText ed_search;
    RelativeLayout rl_num_notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();

        fillData();
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
        adapter = new DrawerRecViewAdapter(this, items, new OnDrawerItemClickListener() {
            @Override
            public void onClick(int position) {
                if(oldPosition != position) {
                    oldPosition = position;
                    startEvent(position);
                }
                mDrawerLayout.closeDrawer(rv_navigation);
//                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        rv_navigation.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_navigation.setLayoutManager(llm);
        rv_navigation.setAdapter(adapter);
        startEvent(0);
        mDrawerLayout.openDrawer(GravityCompat.START);

//        mDrawerLayout.openDrawer(rv_navigation);
    }

    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onStart() {
        super.onStart();


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

    private void startEvent(int position) {
        switch (position) {
            case 0:
                //my posts
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new MyPosts()).commit();
                setTitle("Questions");
                break;
            case 1:
                //profile
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Profile()).commit();
                setTitle(itemsTitles.get(position-1));
                break;
            case 2:
                //search
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new SearchAndFavorite()).commit();
                setTitle(itemsTitles.get(position-1) + " and Favorite");
                break;
            case 3:
                //logout
                Intent intent = new Intent(this, LoginScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case 4:
                //settings
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Settings()).commit();
                setTitle(itemsTitles.get(position-1));
                break;
            case 5:
                //terms
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new TermsFragment()).commit();
                setTitle(itemsTitles.get(position-1));
                break;
            case 6:
                //about
                oldPosition = -1;
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new AboutUs()).commit();
                setTitle(itemsTitles.get(position-1));
                break;
        }
        defaultState();


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
