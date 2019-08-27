package com.eventyay.organizer.core.notification.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.auth.AuthHolder;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.notification.Notification;
import com.eventyay.organizer.data.notification.NotificationRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.raizlabs.android.dbflow.structure.BaseModel;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class NotificationsViewModel extends ViewModel {

    private final List<Notification> notifications = new ArrayList<>();
    private final NotificationRepository notificationRepository;
    private final AuthHolder authHolder;
    private final DatabaseChangeListener<Notification> notificationsChangeListener;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Notification>> notificationsLiveData =
            new SingleEventLiveData<>();

    @Inject
    public NotificationsViewModel(
            NotificationRepository notificationRepository,
            AuthHolder authHolder,
            DatabaseChangeListener<Notification> notificationsChangeListener) {
        this.notificationRepository = notificationRepository;
        this.authHolder = authHolder;
        this.notificationsChangeListener = notificationsChangeListener;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Notification>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public DatabaseChangeListener<Notification> getNotificationsChangeListener() {
        return notificationsChangeListener;
    }

    public void loadNotifications(boolean forceReload) {

        compositeDisposable.add(
                getNotificationSource(forceReload)
                        .doOnSubscribe(disposable -> progress.setValue(true))
                        .doFinally(() -> progress.setValue(false))
                        .toList()
                        .subscribe(
                                notificationsList -> {
                                    Collections.reverse(notificationsList);
                                    notifications.clear();
                                    notifications.addAll(notificationsList);
                                    notificationsLiveData.setValue(notifications);
                                },
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<Notification> getNotificationSource(boolean forceReload) {
        if (!forceReload && !notifications.isEmpty()) {
            return Observable.fromIterable(notifications);
        } else {
            return notificationRepository.getNotifications(authHolder.getIdentity(), forceReload);
        }
    }

    public void listenChanges() {
        notificationsChangeListener.startListening();
        notificationsChangeListener
                .getNotifier()
                .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
                .filter(action -> action.equals(BaseModel.Action.INSERT))
                .subscribeOn(Schedulers.io())
                .subscribe(notificationsModelChange -> loadNotifications(false), Logger::logError);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
