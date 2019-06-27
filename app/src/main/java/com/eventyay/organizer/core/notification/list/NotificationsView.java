package com.eventyay.organizer.core.notification.list;

import com.eventyay.organizer.data.notification.Notification;

import java.util.List;

public interface NotificationsView {

    void showError(String error);

    void showProgress(boolean show);

    void onRefreshComplete(boolean success);

    void showResults(List<Notification> notifications);

    void showEmptyView(boolean show);
}
