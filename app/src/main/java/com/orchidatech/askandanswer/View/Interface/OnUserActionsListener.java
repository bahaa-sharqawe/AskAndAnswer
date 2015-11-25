package com.orchidatech.askandanswer.View.Interface;

/**
 * Created by Bahaa on 18/11/2015.
 */
public interface OnUserActionsListener {
    void onLike(long commentId);
    void onDislike(long commentId);
}
