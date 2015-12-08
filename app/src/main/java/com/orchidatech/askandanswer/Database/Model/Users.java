package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by Bahaa on 12/11/2015.
 */
@Table(name = Users.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Users extends Model {

    public static class FIELDS {
        public static final String TABLE_NAME = "Users";

        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_FNAME = "F_NAME";
        public static final String COLUMN_LNAME = "L_NAME";
        public static final String COLUMN_USERNAME = "UERNAME";
        public static final String COLUMN_EMAIL = "EMAIL";
        public static final String COLUMN_PASSWORD = "PASSWORD";
        public static final String COLUMN_IMAGE = "IMAGE";
        public static final String COLUMN_CREATION_DATE = "CREATION_DATE";
        public static final String COLUMN_ACTIVE = "ACTIVE";
        public static final String COLUMN_LAST_LOGIN = "LAST_LOGIN";
        public static final String COLUMN_MOBILE = "MOBILE";
        public static final String COLUMN_IS_PUBLIC_PROFILE = "IS_PUBLIC_PROFILE";
        public static final String COLUMN_CODE = "CODE";
        public static final String COLUMN_ASKS = "ASKS";
        public static final String COLUMN_ANSWERS = "ANSWERS";
        public static final String COLUMN_RATING = "RATING";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_FNAME)
    public String fname;

    @Column(name = FIELDS.COLUMN_LNAME)
    public String lname;

    @Column(name = FIELDS.COLUMN_USERNAME)
    public String username;

    @Column(name = FIELDS.COLUMN_EMAIL)
    public String email;

    @Column(name = FIELDS.COLUMN_PASSWORD)
    public String password;

    @Column(name = FIELDS.COLUMN_IMAGE)
    public String image;

    @Column(name = FIELDS.COLUMN_CREATION_DATE)
    public long creationDate;

    @Column(name = FIELDS.COLUMN_ACTIVE)
    public int active;

    @Column(name = FIELDS.COLUMN_LAST_LOGIN)
    public long lastLogin;

    @Column(name = FIELDS.COLUMN_MOBILE)
    public String mobile;

    @Column(name = FIELDS.COLUMN_IS_PUBLIC_PROFILE)
    public int isPublicProfile;

    @Column(name = FIELDS.COLUMN_CODE)
    public String code;

    @Column(name = FIELDS.COLUMN_ANSWERS)
    public int answers;

    @Column(name = FIELDS.COLUMN_ASKS)
    public int asks;

    @Column(name = FIELDS.COLUMN_RATING)
    public float rating;

    public Users() {
        super();
    }

    public Users(long serverID, String fname, String lname, String username, String email, String password, String image, long creationDate, int active, long lastLogin, String mobile, int isPublicProfile, String code, int answers, int asks, float rating) {
       super();
        this.serverID = serverID;
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
        this.creationDate = creationDate;
        this.active = active;
        this.lastLogin = lastLogin;
        this.mobile = mobile;
        this.isPublicProfile = isPublicProfile;
        this.code = code;
        this.answers = answers;
        this.asks = asks;
        this.rating = rating;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getIsPublicProfile() {
        return isPublicProfile;
    }

    public void setIsPublicProfile(int isPublicProfile) {
        this.isPublicProfile = isPublicProfile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAsks() {
        return asks;
    }

    public void setAsks(int asks) {
        this.asks = asks;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
