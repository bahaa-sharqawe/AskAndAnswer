package com.orchidatech.askandanswer.Database.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class Settings extends Model {
    public static class FIELDS {
        public static final String TABLE_NAME = "Settings";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_SETTING_KEY = "SETTING_KEY";
        public static final String COLUMN_SETTING_VALUE = "SETTING_VALUE";
    }
    @Column(name = FIELDS.COLUMN_SETTING_KEY)
    private String key;

    @Column(name = FIELDS.COLUMN_SETTING_VALUE)
    private int value;

    public Settings() {
        super();
    }

    public Settings(String key, int value) {
        super();
        this.key = key;
        this.value = value;
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
