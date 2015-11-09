package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.orchidatech.askandanswer.Entity.Category;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

/**
 * Created by Bahaa on 6/11/2015.
 */
public class CategoriesAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> categories;
    ArrayList<Category> selectedCategories;

    public CategoriesAdapter(Context context, ArrayList<Category> categories){
        this.context = context;
        this.categories = categories;
        selectedCategories = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView iv_checkbox;
        final ImageView iv_checked;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cat_listview_item, null, false);
        }
        iv_checkbox = (ImageView) convertView.findViewById(R.id.iv_checkbox);
        iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);

        if(categories.get(position).getChecked()){
            selectedCategories.add(categories.get(position));
            iv_checkbox.setVisibility(View.INVISIBLE);
            iv_checked.setVisibility(View.VISIBLE);
        }else{
            iv_checkbox.setVisibility(View.VISIBLE);
            iv_checked.setVisibility(View.INVISIBLE);
        }

        iv_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategories.add(categories.get(position));
                v.setVisibility(View.INVISIBLE);
                iv_checked.setVisibility(View.VISIBLE);
            }
        });
        iv_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategories.remove(categories.get(position));
                v.setVisibility(View.INVISIBLE);
                iv_checkbox.setVisibility(View.VISIBLE);
            }
        });
        return convertView;
    }
    public ArrayList<Category> getSelectedCategories(){ // called when next pressed
        return selectedCategories;
    }
}
