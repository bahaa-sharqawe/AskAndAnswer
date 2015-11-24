package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Posts;

import java.util.ArrayList;

/**
 * Created by Bahaa on 24/11/2015.
 */
public interface OnMyAsksFetched {
    void onSuccess(ArrayList<Posts> userFavPosts, long last_id);
    void onFail(String error, int errorCode);
}
