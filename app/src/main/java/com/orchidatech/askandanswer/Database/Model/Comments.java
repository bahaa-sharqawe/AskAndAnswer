package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Comments.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Comments extends Model{

    public static class FIELDS {
        public static final String TABLE_NAME = "Comments";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_TEXT = "TEXT";
        public static final String COLUMN_IMAGE = "IMAGE";
        public static final String COLUMN_DATE = "DATE";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_POST_ID = "POST_ID";
        public static final String COLUMN_LIKES = "LIKES";
        public static final String COLUMN_DISLIKES= "DISLIKES";
        public static final String COLUMN_CURRENT_USER_ACTION= "USER_ACTION";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_TEXT)
    public String text;

    @Column(name = FIELDS.COLUMN_IMAGE)
    public String image;

    @Column(name = FIELDS.COLUMN_DATE)
    public long date;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    @Column(name = FIELDS.COLUMN_POST_ID)
    public long postID;

    @Column(name = FIELDS.COLUMN_LIKES)
    public int likes;

    @Column(name = FIELDS.COLUMN_DISLIKES)
    public int disLikes;

    @Column(name = FIELDS.COLUMN_CURRENT_USER_ACTION)
    public int user_action;

    public Comments() {
        super();
    }

    public Comments(long serverID, String text, String image, long date, long userID, long postID, int likes, int disLikes, int user_action) {
        super();
        this.serverID = serverID;
        this.text = text;
        this.image = image;
        this.date = date;
        this.userID = userID;
        this.postID = postID;
        this.likes = likes;
        this.disLikes = disLikes;
        this.user_action = user_action;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getPostID() {
        return postID;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDisLikes() {
        return disLikes;
    }

    public void setDisLikes(int disLikes) {
        this.disLikes = disLikes;
    }

    public int getUser_action() {
        return user_action;
    }

    public void setUser_action(int user_action) {
        this.user_action = user_action;
    }
}
