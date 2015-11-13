package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = User_Categories.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class User_Categories {

    public static class FIELDS{
        public static final String TABLE_NAME = "User_Categories";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long serverID;

    @Column(name = FIELDS.COLUMN_USER_ID)
    private long userID;

    @Column(name = FIELDS.COLUMN_CATEGORY_ID)
    private long categoryID;

    public User_Categories() {
        super();
    }

    public User_Categories(long serverID, long userID, long categoryID) {
        super();
        this.serverID = serverID;
        this.userID = userID;
        this.categoryID = categoryID;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }
}
