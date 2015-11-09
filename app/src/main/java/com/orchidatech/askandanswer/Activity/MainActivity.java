package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.Fragment.AboutUs;
import com.orchidatech.askandanswer.Fragment.Profile;
import com.orchidatech.askandanswer.Fragment.SearchAndFavorite;
import com.orchidatech.askandanswer.Fragment.Settings;
import com.orchidatech.askandanswer.Fragment.TermsFragment;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.DrawerRecViewAdapter;
import com.orchidatech.askandanswer.View.Interface.OnDrawerItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    ArrayList<DrawerItem> items;
    ArrayList<String> itemsTitles;
    private TypedArray navMenuIcons;
    DrawerRecViewAdapter adapter;
    RecyclerView rv_navigation;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();

        fillData();

        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name){ // null => toolbar

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
//        mDrawerLayout.openDrawer(rv_navigation);
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
    private void setCustomActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#fff"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

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
        switch (position){
            case 0:
                //profile
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Profile()).commit();
                setTitle(itemsTitles.get(position));
                break;
            case 1:
                //search
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new SearchAndFavorite()).commit();
                setTitle(itemsTitles.get(position));
                break;
            case 2:
                //logout
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case 3:
                //settings
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Settings()).commit();
                setTitle(itemsTitles.get(position));
                break;
            case 4:
                //terms
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new TermsFragment()).commit();
                setTitle(itemsTitles.get(position));
                break;
            case 5:
                //about
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new AboutUs()).commit();
                setTitle(itemsTitles.get(position));
                break;
        }

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
}
