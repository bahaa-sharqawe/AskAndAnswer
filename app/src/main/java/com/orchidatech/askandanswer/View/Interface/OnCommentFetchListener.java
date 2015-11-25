package com.orchidatech.askandanswer.View.Interface;

import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Posts;

import java.util.ArrayList;

/**
 * Created by Bahaa on 25/11/2015.
 */
public interface OnCommentFetchListener {
    void onSuccess(ArrayList<Comments> comments, long last_id);
    void onFail(String error, int errorCode);
}
