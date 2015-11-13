package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Categories.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Categories {

    public static class FIELDS{
        public static final String TABLE_NAME = "Categories";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long serverID;

    @Column(name = FIELDS.COLUMN_NAME)
    private String name;

    @Column(name = FIELDS.COLUMN_DESCRIPTION)
    private String description;

    public Categories() {
        super();
    }

    public Categories(long serverID, String name, String description) {
        this.serverID = serverID;
        this.name = name;
        this.description = description;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
