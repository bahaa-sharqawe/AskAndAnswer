package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Category.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Category extends Model{


    private boolean enabled;

    public static class FIELDS {
        public static final String TABLE_NAME = "Categories";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_NAME)
    public String name;

    @Column(name = FIELDS.COLUMN_DESCRIPTION)
    public String description;

    public boolean isChecked;

    public Category() {
        super();
    }

    public Category(long serverID, String name, String description) {
        super();
        this.serverID = serverID;
        this.name = name;
        this.description = description;
    }

    public Category(long serverID, String name, String description, boolean isChecked) {
        super();
        this.serverID = serverID;
        this.name = name;
        this.description = description;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
