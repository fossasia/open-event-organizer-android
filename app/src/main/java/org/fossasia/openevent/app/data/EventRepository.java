package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.cache.ICacheModel;
import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.NetworkService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventRepository implements IEventRepository {

    public static final String ORGANIZER = "user";
    public static final String EVENT = "event";
    public static final String EVENTS = "events";
    public static final String ATTENDEES = "attendees";

    private ICacheModel cacheModel;
    private EventService eventService;

    private String authorization;
    private IUtilModel utilModel;

    public EventRepository(IUtilModel utilModel, ICacheModel cacheModel, EventService eventService) {
        this.utilModel = utilModel;
        this.cacheModel = cacheModel;
        this.eventService = eventService;

        authorization = NetworkService.formatToken(utilModel.getToken());
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

    @Override
    public Observable<User> getOrganiser(boolean reload) {
        return getData(() -> eventService.getUser(authorization), ORGANIZER, reload);
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        return getData(() -> eventService.getEvent(eventId), EVENT + eventId, reload);
    }

    @Override
    public Observable<List<Attendee>> getAttendees(long eventId, boolean reload) {
        return getData(() -> eventService.getAttendees(eventId, authorization), ATTENDEES + eventId, reload);
    }

    @Override
    public Observable<List<Event>> getEvents(boolean reload) {
        return getData(() -> eventService.getEvents(authorization), EVENTS, reload);
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

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
