package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bahaa on 13/11/2015.
 */
@Table(name = Notifications.FIELDS.TABLE_NAME, id = BaseColumns._ID)
public class Notifications extends Model {
    public static class FIELDS {
        public static final String TABLE_NAME = "Notifications";
        public static final String COLUMN_SERVER_ID = "SERVER_ID";
        public static final String COLUMN_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
        public static final String COLUMN_OBJECT_ID = "OBJECT_ID";
        public static final String COLUMN_NOTIFICATION_TEXT = "NOTIFICATION_TEXT";
        public static final String COLUMN_DATE = "DATE";
        public static final String COLUMN_IS_DONE = "IS_DONE";
        public static final String COLUMN_USER_PHOTO = "USER_PHOTO";
        public static final String COLUMN_F_NAME = "F_NAME";
        public static final String COLUMN_L_NAME = "L_NAME";
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long serverID;

    @Column(name = FIELDS.COLUMN_NOTIFICATION_TYPE)
    public int notificationType;

    @Column(name = FIELDS.COLUMN_OBJECT_ID)
    public long objectID;

    @Column(name = FIELDS.COLUMN_NOTIFICATION_TEXT)
    public String notificationText;

    @Column(name = FIELDS.COLUMN_DATE)
    public long date;

    @Column(name = FIELDS.COLUMN_IS_DONE)
    public int isDone;

    @Column(name = FIELDS.COLUMN_USER_PHOTO)
    public String user_photo;

    @Column(name = FIELDS.COLUMN_F_NAME)
    public String f_name;

    @Column(name = FIELDS.COLUMN_L_NAME)
    public String l_name;

    public Notifications() {
        super();
    }

    public Notifications(long serverID, int notificationType, long objectID, String notificationText, long date, int isDone, String user_photo, String f_name, String l_name) {
        super();
        this.serverID = serverID;
        this.notificationType = notificationType;
        this.objectID = objectID;
        this.notificationText = notificationText;
        this.date = date;
        this.isDone = isDone;
        this.user_photo = user_photo;
        this.f_name = f_name;
        this.l_name = l_name;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public long getObjectID() {
        return objectID;
    }

    public void setObjectID(long objectID) {
        this.objectID = objectID;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }
}
