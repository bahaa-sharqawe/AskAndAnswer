package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Comments.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Comments {

    public static class FIELDS {
        public static final String TABLE_NAME = "Comments";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_TEXT = "TEXT";
        public static final String COLUMN_IMAGE = "IMAGE";
        public static final String COLUMN_DATE = "DATE";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_POST_ID = "POST_ID";
        public static final String COLUMN_IS_HIDDEN = "IS_HIDDEN";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long serverID;

    @Column(name = FIELDS.COLUMN_TEXT)
    private String text;

    @Column(name = FIELDS.COLUMN_IMAGE)
    private String image;

    @Column(name = FIELDS.COLUMN_DATE)
    private long date;

    @Column(name = FIELDS.COLUMN_USER_ID)
    private long userID;

    @Column(name = FIELDS.COLUMN_POST_ID)
    private long postID;

    @Column(name = FIELDS.COLUMN_IS_HIDDEN)
    private int isHidden;

    public Comments() {
        super();
    }

    public Comments(long serverID, String text, String image, long date, long userID, long postID, int isHidden) {
        this.serverID = serverID;
        this.text = text;
        this.image = image;
        this.date = date;
        this.userID = userID;
        this.postID = postID;
        this.isHidden = isHidden;
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

    public int getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
    }
}
