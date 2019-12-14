package com.eventyay.organizer.core.notification.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.data.notification.Notification;
import com.eventyay.organizer.databinding.NotificationItemBinding;

public class NotificationsViewHolder extends RecyclerView.ViewHolder {

    private final NotificationItemBinding binding;

    public NotificationsViewHolder(NotificationItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Notification notification) {
        binding.setNotification(notification);
        binding.executePendingBindings();
    }
}
