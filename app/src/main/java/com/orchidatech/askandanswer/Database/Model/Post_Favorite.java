package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Post_Favorite.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Post_Favorite extends Model{

    public static class FIELDS {
        public static final String TABLE_NAME = "Post_Favorite";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_POST_ID = "POST_ID";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_DATE = "DATE";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_POST_ID)
    public long postID;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    @Column(name = FIELDS.COLUMN_DATE)
    public long date;

    public Post_Favorite() {
        super();
    }

    public Post_Favorite(long serverID, long postID, long userID, long date) {
        super();
        this.serverID = serverID;
        this.postID = postID;
        this.userID = userID;
        this.date = date;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public long getPostID() {
        return postID;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
