package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EventRepository implements IEventRepository {

    private IDatabaseRepository databaseRepository;
    private EventService eventService;

    private IUtilModel utilModel;

    @Inject
    public EventRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        this.utilModel = utilModel;
        this.databaseRepository = databaseRepository;
        this.eventService = eventService;
    }

    private String getAuthorization() {
        return Utils.formatToken(utilModel.getToken());
    }

    private <T> Observable<T> getAbstractObservable(boolean reload, Observable<T> diskObservable, Observable<T> networkObservable) {
        return
            Observable.defer(() -> {
                if (reload)
                    return Observable.empty();
                else
                    return diskObservable
                        .doOnNext(item -> Timber.d("Loaded %s From Disk on Thread %s",
                            item.getClass(), Thread.currentThread().getName()));
            }).switchIfEmpty(
                Observable.just(utilModel.isConnected())
                    .flatMap(connected -> {
                        if (connected)
                            return networkObservable
                                .doOnNext(item -> Timber.d("Loaded %s From Network on Thread %s",
                                    item.getClass(), Thread.currentThread().getName()));
                        else
                            return Observable.error(new Throwable(Constants.NO_NETWORK));
                    }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<User> getOrganiser(boolean reload) {
        Observable<User> diskObservable = Observable.defer(() ->
            databaseRepository
                .getAllItems(User.class)
                .firstElement()
                .toObservable()
        );

        Observable<User> networkObservable = Observable.defer(() ->
            eventService
                .getUser(getAuthorization())
                .doOnNext(user -> databaseRepository
                    .save(User.class, user)
                    .subscribe()
                )
        );

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository
                .getItems(Event.class, Event_Table.id.eq(eventId))
                .filter(Event::isComplete)
                .take(1)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService
                .getEvent(eventId)
                .doOnNext(event -> {
                    event.setComplete(true);
                    databaseRepository
                        .save(Event.class, event)
                        .subscribe();
                })
        );

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    @Override
    public Observable<Event> getEvents(boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository.getAllItems(Event.class)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService.getEvents(getAuthorization())
                .doOnNext(events -> databaseRepository.deleteAll(Event.class)
                    .concatWith(databaseRepository.saveList(Event.class, events))
                    .subscribe())
                .flatMapIterable(events -> events))
                .doOnEach(eventNotification -> {
                    // Download all complete events in one go
                    if (!eventNotification.isOnNext())
                        return;
                    Event event = eventNotification.getValue();
                    getEvent(event.getId(), false)
                        .subscribe(
                            eventDownloaded ->
                                Timber.d("Downloaded complete event %s", eventDownloaded.getName()),
                            Timber::e);
                });

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    @Override
    public Observable<Attendee> getAttendees(long eventId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Attendee.class, Attendee_Table.eventId.eq(eventId))
        );

        Observable<Attendee> networkObservable = Observable.defer(() ->
            eventService.getAttendees(eventId, getAuthorization())
                .flatMapIterable(attendees -> attendees)
                .doOnNext(attendee -> attendee.setEventId(eventId))
                .toList()
                .toObservable()
                .doOnNext(attendees -> databaseRepository.deleteAll(Attendee.class)
                    .concatWith(databaseRepository.saveList(Attendee.class, attendees))
                    .subscribe())
                .flatMapIterable(attendees -> attendees));

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    /**
     * Fully network oriented task, no fetching from cache, but saving in it is a must
     * @param eventId The ID of event for which we want to change the attendee
     * @param attendeeId The ID of the attendee of whom the check is to be toggled
     * @return Observable defining the process of toggling
     */
    @Override
    public Observable<Attendee> toggleAttendeeCheckStatus(long eventId, long attendeeId) {
        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.toggleAttendeeCheckStatus(eventId, attendeeId, getAuthorization())
            .map(attendee -> {
                // Setting stubbed model to define relationship between event and attendee
                attendee.setEventId(eventId);
                databaseRepository.update(Attendee.class, attendee).subscribe();
                return attendee;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
