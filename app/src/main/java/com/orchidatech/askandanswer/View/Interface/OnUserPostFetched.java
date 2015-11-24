package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Posts;

import java.util.ArrayList;

/**
 * Created by Bahaa on 22/11/2015.
 */
public interface OnUserPostFetched {
    void onSuccess(ArrayList<Posts> userPosts, long last_id);
    void onFail(String error, int errorCode);
}
