package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Users;

/**
 * Created by Bahaa on 24/11/2015.
 */
public interface OnUserInfoFetched {
    void onDataFetched(Users users);
    void onFail(String error);
}
