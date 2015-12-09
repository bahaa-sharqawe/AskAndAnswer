package com.orchidatech.askandanswer.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.R;

import java.util.List;

/**
 * Created by Bahaa on 8/12/2015.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>{
    Activity activity;
    List<Notifications> notifications;

    public NotificationsAdapter(Activity activity, List<Notifications> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }

    @Override
    public NotificationsAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.NotificationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public NotificationViewHolder(View itemView) {
            super(itemView);
        }
    }
}
