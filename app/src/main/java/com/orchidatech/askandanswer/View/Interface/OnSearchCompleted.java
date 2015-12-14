package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Posts;

import java.util.ArrayList;

/**
 * Created by Bahaa on 25/11/2015.
 */
public interface OnSearchCompleted {
    void onSuccess(ArrayList<Posts> searchResult, long last_id);
    void onFail(String error, int error_code);
}
