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
    }

    @Column(name = FIELDS.COLUMN_SERVER_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long serverID;

    @Column(name = FIELDS.COLUMN_NOTIFICATION_TYPE)
    private int notificationType;

    @Column(name = FIELDS.COLUMN_OBJECT_ID)
    private long objectID;

    @Column(name = FIELDS.COLUMN_NOTIFICATION_TEXT)
    private String notificationText;

    @Column(name = FIELDS.COLUMN_DATE)
    private long date;

    @Column(name = FIELDS.COLUMN_IS_DONE)
    private int isDone;

    public Notifications() {
        super();
    }

    public Notifications(long serverID, int notificationType, long objectID, String notificationText, long date, int isDone) {
        this.serverID = serverID;
        this.notificationType = notificationType;
        this.objectID = objectID;
        this.notificationText = notificationText;
        this.date = date;
        this.isDone = isDone;
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
}
