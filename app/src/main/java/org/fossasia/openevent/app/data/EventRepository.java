package org.fossasia.openevent.app.data;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.cache.ICacheModel;
import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EventRepository implements IEventRepository {

    public static final String ATTENDEES = "attendees";

    private IDatabaseRepository databaseRepository;
    private ICacheModel cacheModel;
    private EventService eventService;

    private String authorization;
    private IUtilModel utilModel;

    public EventRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository,
                           ICacheModel cacheModel, EventService eventService) {
        this.utilModel = utilModel;
        this.databaseRepository = databaseRepository;
        this.cacheModel = cacheModel;
        this.eventService = eventService;

        authorization = Utils.formatToken(utilModel.getToken());
    }

    interface EventServiceOperation<T> {
        Observable<T> getDataObservable();
    }

    /**
     * General parametrized method for loading data from service while checking connection
     * and respecting reload state
     */
    private <T> Observable<T> getData(EventServiceOperation<T> eventServiceOperation, String key, boolean reload) {
        T cachedData = (T) cacheModel.getValue(key);

        // Do not use cache if reloading
        if(!reload && cachedData != null)
            return Observable.just(cachedData);

        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventServiceOperation.getDataObservable()
            .doOnNext(attendees -> cacheModel.saveObject(key, attendees))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private <T extends BaseModel> Observable<T> getAbstractObservable(boolean reload, Observable<T> diskObservable, Observable<T> networkObservable) {
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
                .getUser(authorization)
                .doOnNext(user -> databaseRepository
                    .save(user)
                    .subscribe()
                )
        );

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository
                .getItem(Event.class, Event_Table.id.eq(eventId))
                .filter(Event::isComplete)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService
                .getEvent(eventId)
                .doOnNext(event -> {
                    event.setComplete(true);
                    databaseRepository
                        .save(event)
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
            eventService.getEvents(authorization)
                .doOnNext(events -> databaseRepository.saveList(Event.class, events)
                    .subscribe())
                .flatMapIterable(events -> events));

        return getAbstractObservable(reload, diskObservable, networkObservable);
    }

    @Override
    public Observable<List<Attendee>> getAttendees(long eventId, boolean reload) {
        return getData(() -> eventService.getAttendees(eventId, authorization), ATTENDEES + eventId, reload);
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

        return eventService.toggleAttendeeCheckStatus(eventId, attendeeId, authorization)
            .doOnNext(attendee -> updateAttendeeList(eventId, attendee))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private void updateAttendeeList(long eventId, Attendee attendee) {
        String key = ATTENDEES + eventId;

        List<Attendee> attendees = (List<Attendee>) cacheModel.getValue(key);

        // No cached results present, no need to update
        if(attendees == null)
            return;

        Utils.indexOf(attendees, attendee, (first, second) -> first.getId() == second.getId())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(position -> {
                // Item not found
                if (position == -1)
                    return;
                attendees.set(position, attendee);
                cacheModel.saveObject(key, attendees);
            });
    }

}
