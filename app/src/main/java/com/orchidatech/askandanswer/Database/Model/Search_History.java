package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Search_History.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Search_History extends Model{

    public static class FIELDS {
        public static final String TABLE_NAME = "Search_History";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_TEXT = "TEXT";
        public static final String COLUMN_IS_DELETED = "IS_DELETED";
        public static final String COLUMN_USER_ID = "USER_ID";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_TEXT)
    public long text;

    @Column(name = FIELDS.COLUMN_IS_DELETED)
    public int isDeleted;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    public Search_History() {
        super();
    }

    public Search_History(long serverID, long text, int isDeleted, long userID) {
        super();
        this.serverID = serverID;
        this.text = text;
        this.isDeleted = isDeleted;
        this.userID = userID;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public long getText() {
        return text;
    }

    public void setText(long text) {
        this.text = text;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
