package com.orchidatech.askandanswer.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Fragment.Timeline;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.Recievers.GcmBroadcastReceiver;

/**
 * Created by Bahaa on 9/12/2015.
 */
public class GcmIntentService extends IntentService{
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private final CharSequence tickerText = "Notification";
    private final CharSequence contentTitle = "Ask And Answer";
    private PendingIntent mContentIntent;
    private Intent mNotificationIntent;

    private Uri soundURI = Uri
            .parse(/*android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString()*/"android.resource://com.orchidatech.askandanswer/"
                    + R.raw.alarm_rooster);
    private long[] mVibratePattern = { 0, 200, 200, 300 };

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
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
                sendNotification(extras.getString("message"));
//                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationIntent = new Intent(getApplicationContext(),
                MainScreen.class);
        mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                mNotificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notificationBuilder = new Notification.Builder(
                getApplicationContext())
                .setTicker(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(msg)
                .setContentIntent(mContentIntent)
                .setSound(soundURI)
                .setVibrate(mVibratePattern);
        Log.i("cvcllvcv", msg);

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                this).setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Ask Andd Answer")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setContentText(msg);

//        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}
