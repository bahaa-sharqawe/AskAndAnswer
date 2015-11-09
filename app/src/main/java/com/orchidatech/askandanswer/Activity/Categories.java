package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.orchidatech.askandanswer.Entity.Category;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CategoriesAdapter;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    ListView lv_categories;
    CategoriesAdapter adapter;
    String[] categoriesTitles;
    ArrayList<Category> categories;
EditText ed_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        setCustomActionBar();
        ed_search = (EditText) this.findViewById(R.id.ed_search);
        lv_categories = (ListView) this.findViewById(R.id.lv_categories);
        categoriesTitles = this.getResources().getStringArray(R.array.categories);
        categories = new ArrayList<>();
        for (int i = 0; i < categoriesTitles.length; i++)
            categories.add(new Category(categoriesTitles[i], i == 0 ? true : false));

        adapter = new CategoriesAdapter(this, categories);
        lv_categories.setAdapter(adapter);
    }
    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Categories");
        toolbar.setTitleTextColor(Color.parseColor("#fff"));
        toolbar.setNavigationIcon(R.drawable.ic_search);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.next) {
            return true;
        }else if(id == android.R.id.home) {//search icon
            if (ed_search.getVisibility() == View.GONE) {
                getSupportActionBar().setTitle("");
                ed_search.setVisibility(View.VISIBLE);
                return true;
            } else {
                //perform searching
                return true;
            }
        }

        return false;
    }

}
