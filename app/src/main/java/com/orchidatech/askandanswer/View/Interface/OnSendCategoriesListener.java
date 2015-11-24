package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.User_Categories;

import java.util.ArrayList;

/**
 * Created by Bahaa on 21/11/2015.
 */
public interface OnSendCategoriesListener {
    void onSendingSuccess();
    void onSendingFail(String error);
}
