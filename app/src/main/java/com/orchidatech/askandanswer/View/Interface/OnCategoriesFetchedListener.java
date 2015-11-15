package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Category;

import java.util.ArrayList;

/**
 * Created by Bahaa on 15/11/2015.
 */
public interface OnCategoriesFetchedListener {
    void onSuccess(ArrayList<Category> categories);
    void onFail(String cause);
}
