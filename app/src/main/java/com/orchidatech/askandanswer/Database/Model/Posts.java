package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Posts.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Posts {

    public static class FIELDS{
    public static final String TABLE_NAME = "Posts";

    public static final String COLUMN_SERVER_ID = "SERVER_ID";
    public static final String COLUMN_TEXT= "TEXT";
    public static final String COLUMN_IMAGE = "IMAGE";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";
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

    @Column(name = FIELDS.COLUMN_CATEGORY_ID)
    private long categoryID;

    @Column(name = FIELDS.COLUMN_IS_HIDDEN)
    private int isHidden;

    public Posts() {
        super();
    }

    public Posts(long serverID, String text, String image, long date, long userID, long categoryID, int isHidden) {
        super();
        this.serverID = serverID;
        this.text = text;
        this.image = image;
        this.date = date;
        this.userID = userID;
        this.categoryID = categoryID;
        this.isHidden = isHidden;
    }
}
