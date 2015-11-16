package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Settings.FIELDS.TABLE_NAME, id = BaseColumns._ID)

public class Settings extends Model {
    public static class FIELDS {
        public static final String TABLE_NAME = "Settings";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_SETTING_KEY = "SETTING_KEY";
        public static final String COLUMN_SETTING_VALUE = "SETTING_VALUE";
        public static final String COLUMN_USER_ID = "USER_ID";

    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    @Column(name = FIELDS.COLUMN_SETTING_KEY)
    public String key;

    @Column(name = FIELDS.COLUMN_SETTING_VALUE)
    public int value;

    public Settings() {
        super();
    }

    public Settings(long serverID, long userID, String key, int value) {
        this.serverID = serverID;
        this.userID = userID;
        this.key = key;
        this.value = value;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
