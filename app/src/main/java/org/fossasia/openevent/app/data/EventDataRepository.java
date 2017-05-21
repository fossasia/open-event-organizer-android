package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.cache.ICacheModel;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.api.EventService;
import org.fossasia.openevent.app.data.network.api.NetworkService;
import org.fossasia.openevent.app.utils.Constants;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventDataRepository implements IEventDataRepository {

    public static final String ORGANIZER = "user";
    public static final String EVENT = "event";
    public static final String EVENTS = "events";
    public static final String ATTENDEES = "attendees";

    private ICacheModel cacheModel;
    private EventService eventService;

    private String authorization;
    private IUtilModel utilModel;

    public EventDataRepository(IUtilModel utilModel) {
        this.utilModel = utilModel;
        cacheModel = ObjectCache.getInstance();
        eventService = NetworkService.getEventService();

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
        return getData(() -> eventService.getEvent(eventId, authorization), EVENT + eventId, reload);
    }

    @Override
    public Observable<List<Attendee>> getAttendees(long eventId, boolean reload) {
        return getData(() -> eventService.getAttendees(eventId, authorization), ATTENDEES + eventId, reload);
    }

    @Override
    public Observable<List<Event>> getEvents(boolean reload) {
        return getData(() -> eventService.getEvents(authorization), EVENTS, reload);
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
