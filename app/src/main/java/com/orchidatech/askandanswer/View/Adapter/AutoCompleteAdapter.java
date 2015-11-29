package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bahaa on 17/11/2015.
 */
public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    Context contex;
    ArrayList<String> unSelectedCategoriestTitles;
    ArrayList<String> originalUnSelectedCategoriesTitles;
    private ArrayFilter mFilter;


    public AutoCompleteAdapter(Context contex, ArrayList<String> unSelectedCategoriestTitles) {
        this.contex = contex;
        this.unSelectedCategoriestTitles = new ArrayList<>(unSelectedCategoriestTitles);
        this.originalUnSelectedCategoriesTitles = new ArrayList<>(unSelectedCategoriestTitles);
    }

    @Override
    public int getCount() {
        return unSelectedCategoriestTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return unSelectedCategoriestTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(unSelectedCategoriestTitles.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void remove(String removedCategory) {
        originalUnSelectedCategoriesTitles.remove(removedCategory);
        notifyDataSetChanged();
    }

    public void add(String addedCategory) {
        originalUnSelectedCategoriesTitles.add(addedCategory);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                ArrayList<String> list = new ArrayList<>(originalUnSelectedCategoriesTitles);
                results.values = list;
                results.count = list.size();
            } else {
                Log.i("sdsdf", constraint.toString());

                String prefixString = constraint.toString().toLowerCase();
                ArrayList<String> values = new ArrayList<>(originalUnSelectedCategoriesTitles);
                int count = values.size();
                ArrayList<String> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    String value = values.get(i);
                    String valueText = value.toLowerCase();
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(valueText);
                    } else {
                        String[] words = valueText.split(" ");
                        int wordCount = words.length;
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            unSelectedCategoriestTitles = (ArrayList<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
