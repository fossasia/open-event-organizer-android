package org.fossasia.openevent.app.data.network.api;

import org.fossasia.openevent.app.contract.model.ICacheModel;
import org.fossasia.openevent.app.contract.model.IEventModel;
import org.fossasia.openevent.app.contract.model.IUtilModel;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.utils.Constants;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RetrofitEventModel implements IEventModel {

    public static final String ORGANIZER = "user";
    public static final String EVENT = "event";
    public static final String EVENTS = "events";

    private ICacheModel cacheModel;
    private EventService eventService;

    private String authorization;
    private IUtilModel utilModel;

    public RetrofitEventModel(IUtilModel utilModel) {
        this.utilModel = utilModel;
        cacheModel = ObjectCache.getInstance();

        authorization = NetworkService.formatToken(utilModel.getToken());
    }

    private void setupEventService() {
        if (eventService == null)
            eventService = NetworkService.getEventService();
    }

    @Override
    public Observable<User> getOrganiser(boolean reload) {
        User cachedUser = (User) cacheModel.getValue(ORGANIZER);

        // Do not use cache if reloading
        if(!reload && cachedUser != null)
            return Observable.just(cachedUser);

        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        setupEventService();

        return eventService
            .getUser(authorization)
            .doOnNext(user -> cacheModel.saveObject(ORGANIZER, user))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private void saveEvents(List<Event> events) {
        // Caching events off the thread as it may take time
        Completable.fromAction(() -> {
            cacheModel.saveObject(EVENTS, events);

            for(Event event: events) {
                cacheModel.saveObject(EVENT + event.getId(), event);
            }
        }).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe();
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        Event cachedEvent = (Event) cacheModel.getValue(EVENT + eventId);

        // Do not use cache if reloading
        if(!reload && cachedEvent != null)
            return Observable.just(cachedEvent);

        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        setupEventService();

        return eventService
            .getEvent(eventId, authorization)
            .doOnNext(event -> cacheModel.saveObject(EVENT + eventId, event))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Event>> getEvents(boolean reload) {
        List<Event> cachedEvents = (List<Event>) cacheModel.getValue(EVENTS);

        // Do not use cache if reloading
        if(!reload && cachedEvents != null)
            return Observable.just(cachedEvents);

        if(!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        setupEventService();

        return eventService
            .getEvents(authorization)
            .doOnNext(this::saveEvents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
