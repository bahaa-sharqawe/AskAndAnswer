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
        public static final String COLUMN_USERNAME = "USERNAME";
        public static final String COLUMN_EMAIL = "EMAIL";
        public static final String COLUMN_PASSWORD = "PASSWORD";
        public static final String COLUMN_IMAGE = "IMAGE";
        public static final String COLUMN_CREATION_DATE = "CREATION_DATE";
        public static final String COLUMN_ACTIVE = "ACTIVE";
        public static final String COLUMN_LAST_LOGIN = "LAST_LOGIN";
        public static final String COLUMN_MOBILE = "MOBILE";
        public static final String COLUMN_IS_PUBLIC_PROFILE = "IS_PUBLIC_PROFILE";
        public static final String COLUMN_CODE = "CODE";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long serverID;

    @Column(name = FIELDS.COLUMN_USERNAME)
    private String username;

    @Column(name = FIELDS.COLUMN_EMAIL)
    private String email;

    @Column(name = FIELDS.COLUMN_PASSWORD)
    private String password;

    @Column(name = FIELDS.COLUMN_IMAGE)
    private String image;

    @Column(name = FIELDS.COLUMN_CREATION_DATE)
    private long creationDate;

    @Column(name = FIELDS.COLUMN_ACTIVE)
    private int active;

    @Column(name = FIELDS.COLUMN_LAST_LOGIN)
    private String lastLogin;

    @Column(name = FIELDS.COLUMN_MOBILE)
    private String mobile;

    @Column(name = FIELDS.COLUMN_IS_PUBLIC_PROFILE)
    private int isPublicProfile;

    @Column(name = FIELDS.COLUMN_CODE)
    private String code;

    public Users() {
        super();
    }

    public Users(long serverID, String username, String email, String password, String image, long creationDate, int active, String lastLogin, String mobile, int isPublicProfile, String code) {
        super();
        this.serverID = serverID;
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

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
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
}
