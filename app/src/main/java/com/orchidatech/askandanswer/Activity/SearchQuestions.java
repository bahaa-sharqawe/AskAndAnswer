package com.orchidatech.askandanswer.Activity;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.orchidatech.askandanswer.R;

public class SearchQuestions extends AppCompatActivity {
        ImageView uncolored_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_questions);
        setCustomActionBar();
        uncolored_logo = (ImageView) this.findViewById(R.id.uncolored_logo);
        resizeLogo();
    }
    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search in Questions");
        toolbar.setTitleTextColor(Color.parseColor("#fff"));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        uncolored_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        uncolored_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       if(id == android.R.id.home){
            return true;
        }
        return false;
    }

}
