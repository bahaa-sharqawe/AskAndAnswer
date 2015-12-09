package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Category;

import java.util.ArrayList;

/**
 * Created by Bahaa on 9/12/2015.
 */
public interface OnDisabledCategorieslistener {
    void onSuccess(ArrayList<Category> disableedCategories);
    void onFail(String cause);
}
