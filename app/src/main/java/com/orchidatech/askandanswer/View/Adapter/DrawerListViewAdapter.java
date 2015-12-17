package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.orchidatech.askandanswer.Entity.DrawerItem;

import java.util.ArrayList;

/**
 * Created by Bahaa on 15/12/2015.
 */
public class DrawerListViewAdapter extends BaseAdapter {
    ArrayList<DrawerItem> items;
    Context context;

    public DrawerListViewAdapter(ArrayList<DrawerItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
