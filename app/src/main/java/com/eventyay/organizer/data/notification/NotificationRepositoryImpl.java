package com.eventyay.organizer.data.notification;

import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;

import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Observable;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationApi notificationApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public NotificationRepositoryImpl(NotificationApi notificationApi, Repository repository) {
        this.notificationApi = notificationApi;
        this.repository = repository;
    }

    @Override
    public Observable<Notification> getNotifications(int userId, boolean reload) {
        Observable<Notification> diskObservable = Observable.defer(() ->
            repository.getItems(Notification.class, Notification_Table.user_id.eq(userId))
        );

        Observable<Notification> networkObservable = Observable.defer(() ->
            notificationApi.getNotifications(userId)
                .doOnNext(notifications -> repository
                    .syncSave(Notification.class, notifications, Notification::getId, Notification_Table.id)
                    .subscribe())
                .flatMapIterable(notifications -> notifications));

        return repository.observableOf(Notification.class)
            .reload(reload)
            .withRateLimiterConfig("Notification", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
