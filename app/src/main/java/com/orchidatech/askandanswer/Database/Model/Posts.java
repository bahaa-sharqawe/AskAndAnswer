package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Posts.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Posts extends Model {

    public static class FIELDS {
        public static final String TABLE_NAME = "Posts";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_TEXT = "TEXT";
        public static final String COLUMN_IMAGE = "IMAGE";
        public static final String COLUMN_DATE = "DATE";
        public static final String COLUMN_USER_ID = "USER_ID";
        public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";
        public static final String COLUMN_IS_HIDDEN = "IS_HIDDEN";
        public static final String COLUMN_COMMENTS_NO = "COMMENTS_NO";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_TEXT)
    public String text;

    @Column(name = FIELDS.COLUMN_IMAGE)
    public String image;

    @Column(name = FIELDS.COLUMN_DATE)
    public long date;

    @Column(name = FIELDS.COLUMN_USER_ID)
    public long userID;

    @Column(name = FIELDS.COLUMN_CATEGORY_ID)
    public long categoryID;

    @Column(name = FIELDS.COLUMN_IS_HIDDEN)
    public int isHidden;

    @Column(name = FIELDS.COLUMN_COMMENTS_NO)
    public int comments_no;

    private int num_likes;
    private int num_dislikes;

    public Posts() {
        super();
    }

    public Posts(long serverID, String text, String image, long date, long userID, long categoryID, int isHidden, int comments_no) {
        super();
        this.serverID = serverID;
        this.text = text;
        this.image = image;
        this.date = date;
        this.userID = userID;
        this.categoryID = categoryID;
        this.isHidden = isHidden;
        this.comments_no = comments_no;
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

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    public int getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
    }

    public int getComments_no() {
        return comments_no;
    }

    public void setComments_no(int comments_no) {
        this.comments_no = comments_no;
    }

    public int getNum_likes() {
        return num_likes;
    }

    public void setNum_likes(int num_likes) {
        this.num_likes = num_likes;
    }

    public int getNum_dislikes() {
        return num_dislikes;
    }

    public void setNum_dislikes(int num_dislikes) {
        this.num_dislikes = num_dislikes;
    }
}
