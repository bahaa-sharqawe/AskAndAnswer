package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Comments;

/**
 * Created by Bahaa on 25/11/2015.
 */
public interface OnCommentAddListener {
    void onAdded(Comments comment);
    void onFail(String error);
}
