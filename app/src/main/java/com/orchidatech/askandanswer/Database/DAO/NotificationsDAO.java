package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Notifications;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class NotificationsDAO {

    public static void addNotification(Notifications newNotification){
        newNotification.save();
    }

    public static void deleteNotification(long notificationServerId){
        new Delete().from(Notifications.class).where(Notifications.FIELDS.COLUMN_SERVER_ID + " = ?", notificationServerId).execute();
    }

    public static Notifications getNotification(long notificationServerId){
        return new Select().from(Notifications.class).where(Notifications.FIELDS.COLUMN_SERVER_ID + " = ?", notificationServerId).executeSingle();
    }

    public static void updateUser(Notifications notification){
        Notifications existNotification = getNotification(notification.getServerID());
        existNotification.setNotificationType(notification.getNotificationType());
        existNotification.setObjectID(notification.getObjectID());
        existNotification.setNotificationText(notification.getNotificationText());
        existNotification.setDate(notification.getDate());
        existNotification.setIsDone(notification.getIsDone());
        existNotification.save();
    }

    public static List<Notifications> getAllNotifications(){
        return new Select().from(Notifications.class).orderBy(Notifications.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllNotifications(){
        new Delete().from(Notifications.class).execute();
    }
}
