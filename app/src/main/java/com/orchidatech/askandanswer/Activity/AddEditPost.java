package com.orchidatech.askandanswer.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.orchidatech.askandanswer.Entity.SpinnerItem;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddEditPost extends AppCompatActivity {
    ArrayList<String> spItemTitles;
    ArrayList<SpinnerItem> spinnerItems;
    Spinner spin_categories;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_post);
        setCustomActionBar();
        spItemTitles = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.categories)));
        spinnerItems = new ArrayList<>();
        for(int i = 0; i < spItemTitles.size(); i++)
            spinnerItems.add(new SpinnerItem(spItemTitles.get(i), false));
//        spItemTitles.add(getResources().getString(R.string.category));//hint
        spinnerItems.add(new SpinnerItem(getResources().getString(R.string.category), true));//hint

        spin_categories = (Spinner) this.findViewById(R.id.spin_categories);
//        adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spItemTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_categories.setAdapter(adapter);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Post");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            return true;
        }
        return false;
    }
    private class SpinnerAdapter extends ArrayAdapter<SpinnerItem>{

        public SpinnerAdapter(Context context, int resource, ArrayList<SpinnerItem> spinnerItems) {
            super(context, resource, spinnerItems);
        }

        @Override
        public int getCount() {
            return super.getCount()-1;
        }

        @Override
        public SpinnerItem getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
    }
}
