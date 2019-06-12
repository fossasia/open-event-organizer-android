package com.eventyay.organizer.data.notification;

import io.reactivex.Observable;

public interface NotificationRepository {

    Observable<Notification> getNotifications(int userId, boolean reload);
}
