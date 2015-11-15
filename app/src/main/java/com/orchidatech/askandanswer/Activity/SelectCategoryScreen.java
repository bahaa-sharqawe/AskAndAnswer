package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Constant.AppSnackBar;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.CategoriesAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectCategoryScreen extends AppCompatActivity {
    private String TAG = SelectCategoryScreen.class.getSimpleName();
    private final int MIN_CATEGORY = 1;
    private final int MAX_CATEGORY = 20;

    RelativeLayout rl_parent;
    ListView lv_categories;
    CircularProgressView pv_load;
    CategoriesAdapter adapter;
    ArrayList<Category> categories;
    ArrayList<Category> original_categories;
    ArrayList<String> titles;
    EditText ed_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        setCustomActionBar();
        initializeFields();
        loadCategories();
    }

    private void filterList(String s) {
        categories.clear();
        for (Category category : original_categories) {
            if (category.getName().toLowerCase().indexOf(s.toString().toLowerCase()) != -1) {
                categories.add(category);
            }
        }
        pv_load.setVisibility(View.GONE);
        lv_categories.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void loadCategories() {
        pv_load.startAnimation();
        lv_categories.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pv_load.setVisibility(View.GONE);
                lv_categories.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }, 1000);
        for (int i = 0; i < titles.size(); i++) {
            Category category = new Category(i + 1, titles.get(i), titles.get(i), i == 0 ? true : false);
            categories.add(category);
            original_categories.add(category);

        }
       /* WebServiceFunctions.getCategories(this, new OnCategoriesFetchedListener() {
            @Override
            public void onSuccess(ArrayList<Categories> newCategories) {
                categories.addAll(newCategories);
                original_categories.addAll(newCategories);
                pv_load.setVisibility(View.GONE);
                lv_categories.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String cause) {
                pv_load.setVisibility(View.GONE);
                lv_categories.setVisibility(View.VISIBLE);
                AppSnackBar.show(rl_parent, cause, Color.RED, Color.WHITE);
            }
        });*/
    }

    private void initializeFields() {
        rl_parent = (RelativeLayout) this.findViewById(R.id.rl_parent);
        pv_load = (CircularProgressView) this.findViewById(R.id.pv_load);
        ed_search = (EditText) this.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pv_load.resetAnimation();
                pv_load.setVisibility(View.VISIBLE);
                lv_categories.setVisibility(View.GONE);
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lv_categories = (ListView) this.findViewById(R.id.lv_categories);
        lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categories.get(position).setIsChecked(!categories.get(position).isChecked());
                adapter.notifyDataSetChanged();
            }
        });
        titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        categories = new ArrayList<>();
        original_categories = new ArrayList<>();
        adapter = new CategoriesAdapter(this, categories);
        lv_categories.setAdapter(adapter);
    }

    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.categories));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
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
            if (validSelectedCount()) {
                storeSelectedCategories();
                startActivity(new Intent(this, MainActivity.class));
            }
            return true;
        } else if (id == android.R.id.home) {//search icon
            if (ed_search.getVisibility() == View.GONE) {
                getSupportActionBar().setTitle("");
                ed_search.setVisibility(View.VISIBLE);
                return true;
            } else {
                //perform searching
                pv_load.resetAnimation();
                pv_load.setVisibility(View.VISIBLE);
                lv_categories.setVisibility(View.GONE);
                filterList(ed_search.getText().toString());
                return true;
            }
        }

        return false;
    }

    private void storeSelectedCategories() {
        for (Category category : original_categories) {
            if (category.isChecked()) {
                //send selected categories to server
                Toast.makeText(getApplicationContext(), category.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validSelectedCount() {
        int numSelected = 0;
        for (Category category : original_categories) {
            if (category.isChecked())
                numSelected++;

        }
        if (numSelected < MIN_CATEGORY) {
            AppSnackBar.show(rl_parent, getString(R.string.BR_CATS_001), Color.RED, Color.WHITE);
            return false;
        } else if (numSelected > MAX_CATEGORY) {
            AppSnackBar.show(rl_parent, getString(R.string.BR_CATS_002), Color.RED, Color.WHITE);
            return false;
        }
        return true;
    }

}
