package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.User_Actions;

/**
 * Created by Bahaa on 30/11/2015.
 */
public interface OnCommentActionListener {
    void onActionSent(User_Actions user_actions);
    void onFail(String status);
}
