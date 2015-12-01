package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = User_Actions.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class User_Actions extends Model {

    public static class FIELDS {
        public static final String TABLE_NAME = "User_Actions";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_COMMENT_ID = "COMMENT_ID";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_DATE = "DATE";
        public static final String ACTION_TYPE = "ACTION_TYPE";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_COMMENT_ID)
    public long commentID;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    @Column(name = FIELDS.COLUMN_DATE)
    public long date;

    @Column(name = FIELDS.ACTION_TYPE)
    public int actionType;

    public User_Actions() {
        super();
    }

    public User_Actions(long serverID, long commentID, long userID, long date, int actionType) {
        super();

        this.serverID = serverID;
        this.commentID = commentID;
        this.userID = userID;
        this.date = date;
        this.actionType = actionType;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public long getCommentID() {
        return commentID;
    }

    public void setCommentID(long commentID) {
        this.commentID = commentID;
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

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}
