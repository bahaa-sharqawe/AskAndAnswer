package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

/**
 * Created by Bahaa on 6/11/2015.
 */
public class CategoriesAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> categories;
//    ArrayList<Categories> selectedCategories;

    public CategoriesAdapter(Context context, ArrayList<Category> categories){
        this.context = context;
        this.categories = categories;
//        selectedCategories = new ArrayList<>();
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
        final TextView tv_category;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cat_listview_item, null, false);
        }
        iv_checkbox = (ImageView) convertView.findViewById(R.id.iv_checkbox);
        iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);
        tv_category = (TextView) convertView.findViewById(R.id.tv_category);
        tv_category.setText(categories.get(position).getName());
        if(categories.get(position).isChecked()){
            iv_checkbox.setVisibility(View.INVISIBLE);
            iv_checked.setVisibility(View.VISIBLE);
            if(categories.get(position).isEnabled())
                iv_checked.setEnabled(true);
            else
                iv_checked.setEnabled(false);

        }else{
            iv_checkbox.setVisibility(View.VISIBLE);
            iv_checked.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
