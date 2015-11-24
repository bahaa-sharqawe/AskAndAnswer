package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Post_Favorite;

import java.util.ArrayList;

/**
 * Created by Bahaa on 23/11/2015.
 */
public interface OnUserFavPostFetched {
   void onSuccess(ArrayList<Post_Favorite> userFavPosts, long last_id);
    void onFail(String error, int errorCode);
}
