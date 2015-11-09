package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orchidatech.askandanswer.Entity.Category;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;
import java.util.Arrays;

public class EditProfile extends AppCompatActivity {
    AutoCompleteTextView auto_categories;
    GridView grid_categories;
    ArrayList<Category> selectedCategories;
    ArrayList<Category> unSelectedCategories;
    ArrayList<String> categories_titles;

    GridViewAdapter gv_adapter;
    AutoCompleteAdapter ac_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setCustomActionBar();
        auto_categories = (AutoCompleteTextView) this.findViewById(R.id.auto_categories);
        grid_categories = (GridView) this.findViewById(R.id.grid_categories);
        selectedCategories = new ArrayList<>();
        unSelectedCategories = new ArrayList<>();
        categories_titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        for(int i = 0; i <categories_titles.size(); i++)
            unSelectedCategories.add(new Category(categories_titles.get(i), false));
        ac_adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        gv_adapter = new GridViewAdapter(this, R.layout.categories_grid_view_item);
        auto_categories.setAdapter(ac_adapter);
        grid_categories.setAdapter(gv_adapter);
        auto_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = unSelectedCategories.get(position);
                unSelectedCategories.remove(selectedCategory);
                selectedCategory.setChecked(true);
                selectedCategories.add(selectedCategory);
                auto_categories.setText("");
                ac_adapter.notifyDataSetChanged();
                gv_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        toolbar.setTitleTextColor(Color.parseColor("#fff"));
        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            return true;
        }else if(id == android.R.id.home){
            return true;
        }
        return false;
    }
    private class GridViewAdapter extends ArrayAdapter<Category>{

        public GridViewAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return selectedCategories.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tv_category;
            ImageView tv_delete;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
               convertView = inflater.inflate(R.layout.categories_grid_view_item, null, false);
            }
            tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            tv_delete = (ImageView) convertView.findViewById(R.id.tv_delete);
            tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category unSelectedCategory = selectedCategories.get(position);
                    selectedCategories.remove(unSelectedCategory);
                    unSelectedCategory.setChecked(false);
                    unSelectedCategories.add(unSelectedCategory);
                    ac_adapter.notifyDataSetChanged();
                    gv_adapter.notifyDataSetChanged();
                }
            });
            tv_category.setText(selectedCategories.get(position).getName());
        return convertView;
       }
    }
    private class AutoCompleteAdapter extends ArrayAdapter<Category>{

        public AutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return unSelectedCategories.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView text1;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
               convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null, false);
            }
            text1 = (TextView) convertView.findViewById(android.R.id.text1);
            text1.setText(unSelectedCategories.get(position).getName());
        return convertView;
       }
    }
}
