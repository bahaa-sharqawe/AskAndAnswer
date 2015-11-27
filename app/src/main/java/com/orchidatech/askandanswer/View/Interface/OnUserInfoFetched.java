package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Users;

/**
 * Created by Bahaa on 24/11/2015.
 */
public interface OnUserInfoFetched {
    void onDataFetched(Users users, int no_answer, int no_ask);
    void onFail(String error);
}
