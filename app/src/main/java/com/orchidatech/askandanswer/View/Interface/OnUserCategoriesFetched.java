package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.User_Categories;

import java.util.ArrayList;

/**
 * Created by Bahaa on 27/11/2015.
 */
public interface OnUserCategoriesFetched {
    void onSuccess(ArrayList<User_Categories> categories);
    void onFail(String cause);
}
