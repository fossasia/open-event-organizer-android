package org.fossasia.openevent.app.data.attendee;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.db.QueryHelper;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.Event_Table;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class AttendeeRepositoryImpl implements AttendeeRepository {

    private final Repository repository;
    private final AttendeeApi attendeeApi;

    @Inject
    public AttendeeRepositoryImpl(Repository repository, AttendeeApi attendeeApi) {
        this.repository = repository;
        this.attendeeApi = attendeeApi;
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendee(long attendeeId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            repository.getItems(Attendee.class, Attendee_Table.id.eq(attendeeId))
                .take(1)
        );

        // There is no use case where we'll need to load single attendee from network
        return repository.observableOf(Attendee.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(Observable.empty())
            .build();
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendees(long eventId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            repository.getItems(Attendee.class, Attendee_Table.event_id.eq(eventId))
        );

        Observable<Attendee> networkObservable = Observable.defer(() ->
            attendeeApi.getAttendees(eventId)
                .doOnNext(attendees -> repository
                    .syncSave(Attendee.class, attendees, Attendee::getId, Attendee_Table.id)
                    .subscribe())
                .flatMapIterable(attendees -> attendees));

        return repository.observableOf(Attendee.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Long> getCheckedInAttendees(long eventId) {
        return new QueryHelper<Attendee>()
            .method(Method.count(), "sum")
            .from(Attendee.class)
            .equiJoin(Event.class, Event_Table.id, Attendee_Table.event_id)
            .where(Attendee_Table.isCheckedIn.eq(true))
            .and(Attendee_Table.event_id.eq(eventId))
            .count()
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    public Completable scheduleToggle(Attendee attendee) {
        return repository
            .update(Attendee.class, attendee)
            .concatWith(completableObserver -> {
                AttendeeCheckInJob.scheduleJob();
                if (!repository.isConnected())
                    completableObserver.onError(new Exception("No network present. Added to job queue"));
            })
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Attendee> toggleAttendeeCheckStatus(Attendee transitAttendee) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return Observable.just(transitAttendee)
            .flatMap(attendee -> {
                // Remove relationships from attendee item
                attendee.setEvent(null);
                attendee.setTicket(null);
                attendee.setOrder(null);
                attendee.setCheckinTimes(DateUtils.formatDateToIso(LocalDateTime.now()));

                return attendeeApi.patchAttendee(attendee.getId(), attendee);
            })
            .doOnNext(attendee -> repository
                .update(Attendee.class, attendee)
                .subscribe())
            .doOnError(throwable -> scheduleToggle(transitAttendee))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * A synchronous method for getting pending attendee check ins
     * @return Pending attendee check ins
     */
    @NonNull
    @Override
    public Observable<Attendee> getPendingCheckIns() {
        return repository.getItems(Attendee.class,
            Attendee_Table.checking.eq(true));
    }

}
