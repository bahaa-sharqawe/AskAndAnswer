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
        if(isExist(newNotification.getServerID())){
            updateNotification(newNotification);
            return;
        }
        Notifications notification = new Notifications();
        notification.date = newNotification.getDate();
        notification.isDone = newNotification.getIsDone();
        notification.notificationText = newNotification.getNotificationText();
        notification.notificationType = newNotification.getNotificationType();
        notification.objectID = newNotification.getObjectID();
        notification.serverID = newNotification.getServerID();
        notification.save();
    }

    public static void deleteNotification(long notificationServerId){
        new Delete().from(Notifications.class).where(Notifications.FIELDS.COLUMN_SERVER_ID + " = ?", notificationServerId).execute();
    }

    public static Notifications getNotification(long notificationServerId){
        return new Select().from(Notifications.class).where(Notifications.FIELDS.COLUMN_SERVER_ID + " = ?", notificationServerId).executeSingle();
    }

    public static void updateNotification(Notifications notification){
        Notifications existNotification = getNotification(notification.getServerID());
        existNotification.notificationType = notification.getNotificationType();
        existNotification.objectID = notification.getObjectID();
        existNotification.notificationText = notification.getNotificationText();
        existNotification.date = notification.getDate();
        existNotification.isDone = notification.getIsDone();
        existNotification.save();
    }

    public static List<Notifications> getAllNotifications(){
        return new Select().from(Notifications.class).orderBy(Notifications.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllNotifications(){
        new Delete().from(Notifications.class).execute();
    }

    private static boolean isExist(long serverID) {
        return getNotification(serverID) != null ;
    }

}
