package com.orchidatech.askandanswer.View.Interface;

/**
 * Created by Bahaa on 16/11/2015.
 */
public interface OnPostEventListener {
    public void onClick(long pid);
    public void onSharePost(long pid);
    public void onCommentPost(long pid);
    public void onFavoritePost(int position, long pid, long uid);
    public void onCategoryClick(long cid, long uid);
}
