package com.eventyay.organizer.core.notification.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.notification.list.viewholder.NotificationsViewHolder;
import com.eventyay.organizer.data.notification.Notification;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsViewHolder> {

    public final List<Notification> notifications;

    public NotificationsAdapter(NotificationsViewModel notificationsViewModel) {
        this.notifications = notificationsViewModel.getNotifications();
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        NotificationsViewHolder notificationsViewHolder= new NotificationsViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.notification_item, viewGroup, false));

        return notificationsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder notificationsViewHolder, int position) {
        notificationsViewHolder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
