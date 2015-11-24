package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Database.Model.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 14/11/2015.
 */
public interface OnLoginListener {
        void onSuccess(long uid, ArrayList<Long> user_categories);
        void onFail(String cause);
}
