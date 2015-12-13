package com.orchidatech.askandanswer.Recievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Database.DAO.NotificationsDAO;
import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bahaa on 9/12/2015.
 */
//public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                GcmIntentService.class.getName());
//        // Start the service, keeping the device awake while it is launching.
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
//    }
//}
public class GcmBroadcastReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private final CharSequence tickerText = "Notification";
    private final CharSequence contentTitle = "Ask And Answer";
    private PendingIntent mContentIntent;
    private Intent mNotificationIntent;

    private Uri soundURI = Uri
            .parse(/*android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString()*/"android.resource://com.orchidatech.askandanswer/"
                    + R.raw.alarm_rooster);
    private long[] mVibratePattern = {0, 200, 200, 300};
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                GcmIntentService.class.getName());
//        // Start the service, keeping the device awake while it is launching.
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
//                sendNotification("Deleted messages on server: "
//                        + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                String message = extras.getString("message");
                Log.i("fdfddffdf", message);

                parsingMSG(message);
            }

        }
    }

    private void parsingMSG(String message) {

//        sendNotification(extras.getString("message"));
        try {
            JSONObject data = new JSONObject(message);
            String userImg = data.getString("userimg");
            JSONObject notifications_obj = data.getJSONObject("notifications");
            int notification_is_done = Integer.parseInt(notifications_obj.getString("is_done"));//is_done = 0 ==> false
            long notification_date = notifications_obj.getLong("created_at");
            long notification_id = Long.parseLong(notifications_obj.getString("id"));
            String notification_text = notifications_obj.getString("text");
            int notification_type = Integer.parseInt(notifications_obj.getString("type"));
            long notification_object_id = Long.parseLong(notifications_obj.getString("object_id"));
            Notifications notifications = new Notifications(notification_id, notification_type, notification_object_id,
                    notification_text, notification_date, notification_is_done, userImg);
            ActiveAndroid.beginTransaction();
            try {
                notifications.save();
//                NotificationsDAO.addNotification(notifications);
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
            Log.i("vcvcvc", NotificationsDAO.getAllNotifications().size() + "");
            sendNotification(notification_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationIntent = new Intent(context,
                MainScreen.class);
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notificationBuilder = new Notification.Builder(
                context)
                .setTicker(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(msg)
                .setContentIntent(mContentIntent)
                .setSound(soundURI)
                .setVibrate(mVibratePattern);
        Log.i("cvcllvcv", msg);
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
