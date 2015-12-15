package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;

/**
 * Created by Bahaa on 14/12/2015.
 */
public interface OnPostDataListener {
    void onSuccess(Posts post, Users owner);
    void onFail(String cause);
}
