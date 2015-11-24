package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.AddEditPost;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

/**
 * Created by Bahaa on 18/11/2015.
 */
public class SpinAdapter extends BaseAdapter{
    Context context;
    ArrayList<User_Categories> user_categories;

    public SpinAdapter(Context context, ArrayList<User_Categories> user_categories) {
        this.context = context;
        this.user_categories = user_categories;
    }

    @Override
    public int getCount() {
        return user_categories.size();
    }

    @Override
    public User_Categories getItem(int position) {
        return user_categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_spinn_item;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item, null, false);
        }
        tv_spinn_item = (TextView) convertView.findViewById(R.id.tv_spinn_item);
        tv_spinn_item.setText(CategoriesDAO.getCategory(user_categories.get(position).getCategoryID()).getName());
        return convertView;

    }
}
