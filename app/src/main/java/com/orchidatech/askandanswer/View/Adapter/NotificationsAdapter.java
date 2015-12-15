package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.NotificationPostView;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bahaa on 8/12/2015.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private FontManager fontManager;
    Activity activity;
    List<Notifications> notifications;

    public NotificationsAdapter(Activity activity, List<Notifications> notifications) {
        this.activity = activity;
        this.notifications = notifications;
        fontManager = FontManager.getInstance(activity.getAssets());
    }

    @Override
    public NotificationsAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationsAdapter.NotificationViewHolder holder, int position) {
        final Notifications notification = notifications.get(position);
        final String notification_text;
//        Users user;
//                if(notification.notificationType == Enum.NOTIFICATIONS.NEW_COMMENT.getNumericType()) {
//                    Comments comment = CommentsDAO.getComment(notification.objectID);
//                    user = UsersDAO.getUser(comment.userID);
//                    notification_text = comment.text;
//                }
//                else {
//                    Posts post = PostsDAO.getPost(notification.objectID);
//                    user = UsersDAO.getUser(post.getUserID());
//                    notification_text = post.text;
//                }
        String date = GNLConstants.DateConversion.getDate(notification.date);
        holder.notif_date.setText(date);
        holder.tv_notification_text.setText(notification.notificationText);
        if (!notification.getUser_photo().equals(URL.DEFAULT_IMAGE))
            Picasso.with(activity).load(Uri.parse(notification.getUser_photo())).into(holder.iv_profile, new Callback() {
                @Override
                public void onSuccess() {
                    holder.tv_person_photo.setVisibility(View.INVISIBLE);
                    holder.iv_profile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.iv_profile.setVisibility(View.INVISIBLE);
                    holder.tv_person_photo.setVisibility(View.VISIBLE);
                    holder.tv_person_photo.setText(notification.f_name.charAt(0) + " " + notification.l_name.charAt(0));
                }
            });
        else {
            holder.iv_profile.setVisibility(View.INVISIBLE);
            holder.tv_person_photo.setVisibility(View.VISIBLE);
            holder.tv_person_photo.setText(notification.f_name.charAt(0) + " " + notification.l_name.charAt(0));
        }
        if (notification.isDone == 0)//not read
            holder.card_notification.setBackgroundColor(Color.parseColor("#eef4ff"));
        else
            holder.card_notification.setBackgroundColor(Color.parseColor("#ffffff"));

        holder.card_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.isDone = 1;
                ///got to post or comment
                notification.save();
                Intent intent = new Intent(activity, NotificationPostView.class);
                intent.putExtra(NotificationPostView.OBJECT_ID, notification.getObjectID());
                intent.putExtra(NotificationPostView.TYPE, notification.getNotificationType());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_person_photo;
        CircleImageView iv_profile;
        TextView tv_notification_text;
        TextView notif_date;
        CardView card_notification;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
            tv_notification_text = (TextView) itemView.findViewById(R.id.tv_notification_text);
            notif_date = (TextView) itemView.findViewById(R.id.notif_date);
            card_notification = (CardView) itemView.findViewById(R.id.card_notification);
            tv_person_photo = (TextView) itemView.findViewById(R.id.tv_person_photo);
            tv_person_photo.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

        }
    }
}
