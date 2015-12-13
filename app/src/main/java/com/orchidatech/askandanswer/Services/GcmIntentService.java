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

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.NotificationsDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Fragment.Timeline;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.Recievers.GcmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

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
                String message = extras.getString("message");
                Log.i("fdfddffdf", message);

                parsingMSG(message);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
//        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }




//    post notification
//    {"post_data":{"image":null,"category_id":"1","updated_at":1449880437000,"user_id":"58","is_hidden":"0","created_at":1449880437000,"id":"194","text":"Hello"},"user_info":{"image":"http:\/\/softplaystore.com\/askandanswer_ws\/public\/users\/default_image.jpg","code":"sMhNddGQ_58","last_login":1449792000000,"l_name":"Al Sharqawi","mobile":null,"active":"0","registration_id":"APA91bEpND9a5cN-AKHXjIHFxO0E17alU8MjihMsQfLVNly59B3mg-xNOX7kchk39u5x_RSf0DkWoIHKI-_pqZ5Zh-IPHKIxUiTU4cqjNFDKcZ1GJEYndP-8MhNrUF3oIgMF_g1MMwyN","created_at":1449841445000,"temp_pass":"$2y$10$mnpLG.mM3G4X6\/YCFABPveGT9Lj2dMTv6PZXuMWOHRmlijrBXAEvm","updated_at":1449863045000,"f_name":"BahaaIddin","is_public":"0","askandanswer":{"no_of_stars":1.02941176471,"no_ask":58,"no_answer":34},"id":"58","email":"bm.sharqawi@gmail.com"},"text":"BahaaIddin Al Sharqawi added new post.","notifications":{"is_done":"0","updated_at":1449880438000,"created_at":1449880438000,"id":"197","text":"BahaaIddin Al Sharqawi added new post.","type":"0","object_id":"194"},"username":"BahaaIddin Al Sharqawi"}

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
            }
            finally {
                ActiveAndroid.endTransaction();
            }
            Log.i("vcvcvc", NotificationsDAO.getAllNotifications().size()+"");

//            JSONObject post_data = data.getJSONObject("post_data");
//            String post_image = post_data.getString("image");
//            long category_id = Long.parseLong(post_data.getString("category_id"));
//            long post_date = post_data.getLong("updated_at");
//            long post_user_id = Long.parseLong(post_data.getString("user_id"));
//            int post_is_hidden = Integer.parseInt(post_data.getString("is_hidden"));
//            long post_id = Long.parseLong(post_data.getString("id"));
//            String post_text = post_data.getString("text");
//            Posts posts = new Posts(post_id, post_text, post_image, post_date, post_user_id, category_id, post_is_hidden, -1, -1, -1, -1);
//            PostsDAO.addPost(posts);
//            //////////////////////////////////////////////////////////////////////////////////////////////////////
//
//            JSONObject post_owner_data = data.getJSONObject("user_info");
//            String user_image = post_owner_data.getString("image");
//            String user_code = post_owner_data.getString("code");
//            long user_last_login = post_owner_data.getLong("last_login");
//            String user_lname = post_owner_data.getString("l_name");
//            String user_mobile = post_owner_data.getString("mobile");
//            int user_active = Integer.parseInt(post_owner_data.getString("active"));
//            long user_created_at = post_owner_data.getLong("created_at");
//            long user_update_at = post_owner_data.getLong("update_at");
//            String user_f_name = post_owner_data.getString("f_name");
//            int user_is_public = Integer.parseInt(post_owner_data.getString("is_public"));
//            JSONObject askandanswer = post_owner_data.getJSONObject("askandanswer");
//            float rating = Float.parseFloat(askandanswer.get("no_of_stars") + "");
//            int no_ask = askandanswer.getInt("no_ask");
//            int no_answer = askandanswer.getInt("no_answer");
//            long post_owner_id = Long.parseLong(post_owner_data.getString("id"));
//            String post_owner_email = post_owner_data.getString("email");
//            Users user = new Users(post_owner_id, user_f_name, user_lname, null, post_owner_email, null, user_image,
//                    user_created_at, user_active, user_last_login, user_mobile, user_is_public, user_code, no_answer, no_ask, rating);
//            UsersDAO.addUser(user);
//            ///////////////////////////////////////////////////////////////////////////////////
//
//     //            //////////////////////////////////////////////////////////////////////////////
//
////    comment notification
////    {"post_data":{"image":null,"category_id":"1","updated_at":1449879616000,"user_id":"58","is_hidden":"0",
//// "created_at":1449879616000,"id":"192","text":"هلت"}
//// ,"user_info":{"image":"http:\/\/softplaystore.com\/askandanswer_ws\/public\/users\/default_image.jpg","code":"sMhNddGQ_58","
//// last_login":1449792000000,"l_name":"Al Sharqawi","mobile":null,"active":"0",
//// "registration_id":"APA91bEpND9a5cN-AKHXjIHFxO0E17alU8MjihMsQfLVNly59B3mg-xNOX7kchk39u5x_RSf0DkWoIHKI-_pqZ5Zh-I
//// PHKIxUiTU4cqjNFDKcZ1GJEYndP-8MhNrUF3oIgMF_g1MMwyN","created_at":1449841445000,
//// "temp_pass":"$2y$10$mnpLG.mM3G4X6\/YCFABPveGT9Lj2dMTv6PZXuMWOHRmlijrBXAEvm","updated_at":1449863045000,
//// "f_name":"BahaaIddin","is_public":"0","askandanswer":{"no_of_stars":1.02941176471,"no_ask":56,"no_answer":34},"id":"58",
//// "email":"bm.sharqawi@gmail.com"},
//// "text":"BahaaIddin Al Sharqawi added new comment on your post.","notifications":{"is_done":"0",
//// "updated_at":1449880331000,"created_at":1449880331000,"id":"195","text":"BahaaIddin Al Sharqawi added new
//// comment on your post.","type":"1","object_id":"314"},
//// "comment_data":{"image":null,"post_id":"192","updated_at":1449880331000,"user_id":"58","created_at":1449880331000,
//// "comment":"هلا","id":"314"},"username":"BahaaIddin Al Sharqawi"
//// ,"user_owner_of_post":{"image":"http:\/\/softplaystore.com\/askandanswer_ws\/public\/users\/default_image.jpg","code":"sMhNddGQ_58","last_login":1449792000000,"l_name":"Al Sharqawi","mobile":null,"active":"0","registration_id":"APA91bEpND9a5cN-AKHXjIHFxO0E17alU8MjihMsQfLVNly59B3mg-xNOX7kchk39u5x_RSf0DkWoIHKI-_pqZ5Zh-IPHKIxUiTU4cqjNFDKcZ1GJEYndP-8MhNrUF3oIgMF_g1MMwyN","created_at":1449841445000,"temp_pass":"$2y$10$mnpLG.mM3G4X6\/YCFABPveGT9Lj2dMTv6PZXuMWOHRmlijrBXAEvm","updated_at":1449863045000,"f_name":"BahaaIddin","is_public":"0","askandanswer":{"no_of_stars":1.02941176471,"no_ask":56,"no_answer":34},"id":"58","email":"bm.sharqawi@gmail.com"}}
////
//            if(notification_type == Enum.NOTIFICATIONS.NEW_COMMENT.getNumericType()){
//                JSONObject comment_data = data.getJSONObject("comment_data");
//                String comment_image = comment_data.getString("image");
//                long comment_post_id = Long.parseLong(comment_data.getString("post_id"));
//                long comment_date = comment_data.getLong("updated_at");
//                long comment_user_id = Long.parseLong(comment_data.getString("user_id"));
//                String comment = comment_data.getString("comment");
//                long comment_id = Long.parseLong(comment_data.getString("id"));
//                Comments comments = new Comments(comment_id, comment, comment_image, comment_date, comment_user_id, comment_post_id, -1, -1);
//                CommentsDAO.addComment(comments);
//                ////////////////////////////////////////////////////////////
//                JSONObject comment_owner_data = comment_data.getJSONObject("user_owner_of_post");
//                String comment_owner_image = comment_owner_data.getString("image");
//                String comment_owner_code = comment_owner_data.getString("code");
//                long comment_owner_last_login = comment_owner_data.getLong("last_login");
//                String comment_owner_lname = comment_owner_data.getString("l_name");
//                String comment_owner_mobile = comment_owner_data.getString("mobile");
//                int comment_owner_active = Integer.parseInt(comment_owner_data.getString("active"));
//                long comment_owner_created_at = comment_owner_data.getLong("created_at");
//                long comment_owner_update_at = comment_owner_data.getLong("update_at");
//                String comment_owner_f_name = comment_owner_data.getString("f_name");
//                int comment_owner_is_public = Integer.parseInt(comment_owner_data.getString("is_public"));
//                JSONObject comment_owner_askandanswer = comment_owner_data.getJSONObject("askandanswer");
//                float comment_owner_rating = Float.parseFloat(comment_owner_askandanswer.get("no_of_stars") + "");
//                int comment_owner_no_ask = comment_owner_askandanswer.getInt("no_ask");
//                int comment_owner_no_answer = comment_owner_askandanswer.getInt("no_answer");
//                long comment_owner_id = Long.parseLong(comment_owner_data.getString("id"));
//                String comment_owner_email = comment_owner_data.getString("email");
//                Users comment_owner_user = new Users(comment_owner_id, comment_owner_f_name, comment_owner_lname, null,
//                        comment_owner_email, null, comment_owner_image,
//                        comment_owner_created_at, comment_owner_active, comment_owner_last_login, comment_owner_mobile,
//                        comment_owner_is_public, comment_owner_code, comment_owner_no_answer, comment_owner_no_ask, comment_owner_rating);
//                UsersDAO.addUser(comment_owner_user);
//            }

            sendNotification(notification_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
