package org.fossasia.openevent.app.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.utils.Constants;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AttendeeRepository extends Repository implements IAttendeeRepository {

    @Inject
    public AttendeeRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendee(long attendeeId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Attendee.class, Attendee_Table.id.eq(attendeeId))
                .take(1)
        );

        // There is no use case where we'll need to load single attendee from network
        return new AbstractObservableBuilder<Attendee>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(Observable.empty())
            .build();
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendees(long eventId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Attendee.class, Attendee_Table.event_id.eq(eventId))
        );

        Observable<Attendee> networkObservable = Observable.defer(() ->
            eventService.getAttendees(eventId)
                .flatMapIterable(attendees -> attendees)
                .toList()
                .toObservable()
                .doOnNext(attendees -> {
                    Timber.d(attendees.toString());

                    databaseRepository.deleteAll(Attendee.class)
                    .concatWith(databaseRepository.saveList(Attendee.class, attendees))
                    .subscribe(() -> Timber.d("Saved Attendees"), Logger::logError);
                })
                .flatMapIterable(attendees -> attendees));

        return new AbstractObservableBuilder<Attendee>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    /**
     * Fully network oriented task, no fetching from cache, but saving in it is a must
     * @param eventId The ID of event for which we want to change the attendee
     * @param attendeeId The ID of the attendee of whom the check is to be toggled
     * @return Observable defining the process of toggling
     */
    @NonNull
    @Override
    public Observable<Attendee> toggleAttendeeCheckStatus(long eventId, long attendeeId) {
        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return databaseRepository.getItems(Attendee.class, Attendee_Table.id.eq(attendeeId))
            .take(1)
            .flatMap(attendee -> {
                // Remove relationships from attendee item
                attendee.setEvent(null);
                attendee.setTicket(null);
                attendee.setOrder(null);

                attendee.setCheckedIn(!attendee.isCheckedIn());

                return eventService.patchAttendee(attendeeId, attendee);
            })
            .doOnNext(attendee ->
                databaseRepository
                    .update(Attendee.class, attendee)
                    .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
