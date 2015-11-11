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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orchidatech.askandanswer.Entity.Category;
import com.orchidatech.askandanswer.Logic.FlowLayout;
import com.orchidatech.askandanswer.Logic.HorizontalFlowLayout;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EditProfile extends AppCompatActivity {
    private static final int WIDTH = 125;

    AutoCompleteTextView auto_categories;
    EditText ed_password;
    ImageView iv_update_password;
    LinearLayout ll_newPassword;
//    GridView grid_categories;
    HorizontalFlowLayout ll_categories;
    ArrayList<Category> selectedCategories;
    ArrayList<Category> unSelectedCategories;
    ArrayList<String> categories_titles;

    GridViewAdapter gv_adapter;
    ArrayAdapter<String> ac_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setCustomActionBar();
        auto_categories = (AutoCompleteTextView) this.findViewById(R.id.auto_categories);
//        grid_categories = (GridView) this.findViewById(R.id.grid_categories);
        ll_categories = (HorizontalFlowLayout) findViewById(R.id.hf_categories);
        selectedCategories = new ArrayList<>();
        unSelectedCategories = new ArrayList<>();
        categories_titles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        for(int i = 0; i <categories_titles.size(); i++)
            unSelectedCategories.add(new Category(categories_titles.get(i), false));
//        ac_adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        ac_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories_titles);

        gv_adapter = new GridViewAdapter();
        auto_categories.setAdapter(ac_adapter);
//        grid_categories.setAdapter(gv_adapter);
        auto_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < categories_titles.size(); i++) {
                    if (categories_titles.get(i).equals(((TextView) view).getText().toString())) {
                        position = i;
                        break;
                    }
                }
                Category selectedCategory = unSelectedCategories.get(position);
                selectedCategory.setChecked(true);
                unSelectedCategories.remove(selectedCategory);
                selectedCategories.add(selectedCategory);//add to gridview
                ac_adapter.remove(categories_titles.remove(position));//remove from autocomplete
                ac_adapter.notifyDataSetChanged();
                gv_adapter.notifyDataSetChanged();
                auto_categories.setText("");
                final View item = LayoutInflater.from(EditProfile.this).inflate(R.layout.categories_grid_view_item, null, false);
                TextView tv_category = (TextView) item.findViewById(R.id.tv_category);
                tv_category.setText(selectedCategory.getName());
                ImageView iv_delete = (ImageView) item.findViewById(R.id.tv_delete);
                LinearLayout ll_delete = (LinearLayout) item.findViewById(R.id.ll_delete);
                ll_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout parent = (LinearLayout) v.getParent();
                        TextView targetCategory = (TextView) parent.findViewById(R.id.tv_category);
                        String targetCategoryTitle = targetCategory.getText().toString();
                        ll_categories.removeView(item);
                        int targetPosition = 0;
                        for(int i = 0; i < selectedCategories.size(); i++){
                            if(selectedCategories.get(i).getName().equals(targetCategoryTitle)){
                                targetPosition = i;
                                break;
                            }
                        }
                        Category deletedCategory = selectedCategories.get(targetPosition);
                        deletedCategory.setChecked(true);
                        unSelectedCategories.add(deletedCategory);
                        selectedCategories.remove(deletedCategory);//add to gridview
                        categories_titles.add(targetCategoryTitle);
                        ac_adapter.add(targetCategoryTitle);//remove from autocomplete
                        ac_adapter.notifyDataSetChanged();
//                        gv_adapter.notifyDataSetChanged();
                    }
                });
//                iv_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ll_categories.removeView(item);
//                        LinearLayout parent = (LinearLayout) v.getParent();
//                        TextView targetCategory = (TextView) parent.findViewById(R.id.tv_category);
//                        String targetCategoryTitle = targetCategory.getText().toString();
//                        int targetPosition = 0;
//                        for(int i = 0; i < selectedCategories.size(); i++){
//                            if(selectedCategories.get(i).getName().equals(targetCategoryTitle)){
//                                targetPosition = i;
//                                break;
//                            }
//                        }
//                        Category deletedCategory = selectedCategories.get(targetPosition);
//                        deletedCategory.setChecked(true);
//                        unSelectedCategories.add(deletedCategory);
//                        selectedCategories.remove(deletedCategory);//add to gridview
//                        categories_titles.add(targetCategoryTitle);
//                        ac_adapter.add(targetCategoryTitle);//remove from autocomplete
//                        ac_adapter.notifyDataSetChanged();
////                        gv_adapter.notifyDataSetChanged();
//                    }
//                });
                HorizontalFlowLayout.LayoutParams params = new HorizontalFlowLayout.LayoutParams(WIDTH, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
//                item.setLayoutParams(params);
                ll_categories.addView(item, params);



            }
        });
        ll_newPassword = (LinearLayout) findViewById(R.id.ll_newPassword);
        ed_password = (EditText) findViewById(R.id.ed_password);
        iv_update_password = (ImageView) findViewById(R.id.iv_update_password);
        iv_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_password.setEnabled(true);
                ed_password.setHint("Current Password");
                v.setVisibility(View.GONE);
                ll_newPassword.setVisibility(View.VISIBLE);
            }
        });

    }
    private void setCustomActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
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
    private class GridViewAdapter extends BaseAdapter{



        @Override
        public int getCount() {
            return selectedCategories.size();
        }

        @Override
        public Category getItem(int position) {
            return selectedCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
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
                    RelativeLayout parent = (RelativeLayout) v.getParent();
                    TextView targetCategory = (TextView) parent.findViewById(R.id.tv_category);
                    String targetCategoryTitle = targetCategory.getText().toString();
                    int targetPosition = 0;
                    for(int i = 0; i < selectedCategories.size(); i++){
                        if(selectedCategories.get(i).getName().equals(targetCategoryTitle)){
                            targetPosition = i;
                            break;
                        }
                    }
                    Category deletedCategory = selectedCategories.get(targetPosition);
                    deletedCategory.setChecked(true);
                    unSelectedCategories.add(deletedCategory);
                    selectedCategories.remove(deletedCategory);//add to gridview
                    categories_titles.add(targetCategoryTitle);
                    ac_adapter.add(targetCategoryTitle);//remove from autocomplete
                    ac_adapter.notifyDataSetChanged();
                    gv_adapter.notifyDataSetChanged();
                }
            });
            tv_category.setText(selectedCategories.get(position).getName());
        return convertView;
       }
    }
//    private class AutoCompleteAdapter extends ArrayAdapter<Category>{
//
//        public AutoCompleteAdapter(Context context, int resource) {
//            super(context, resource);
//        }
//
//        @Override
//        public int getCount() {
//            return unSelectedCategories.size();
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            TextView text1;
//            if(convertView == null){
//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//               convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null, false);
//            }
//            text1 = (TextView) convertView.findViewById(android.R.id.text1);
//            text1.setText(unSelectedCategories.get(position).getName());
//        return convertView;
//       }
//    }

}
