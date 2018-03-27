package org.fossasia.openevent.app.core.event.list;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.repository.IEventRepository;
import org.fossasia.openevent.app.utils.service.DateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class EventsPresenter extends BasePresenter<IEventsView> {

    private final List<Event> events = new ArrayList<>();

    private final IEventRepository eventsDataRepository;

    public static final int SORTBYDATE = 0;
    public static final int SORTBYNAME = 1;

    @Inject
    public EventsPresenter(IEventRepository eventsDataRepository) {
        this.eventsDataRepository = eventsDataRepository;
    }

    @Override
    public void start() {
        loadUserEvents(false);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void sortBy(int criteria) {
        if (criteria == SORTBYNAME)
            Collections.sort(events, (e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
        else {
            Collections.sort(events, DateService::compareEventDates);
        }
    }

    public void loadUserEvents(boolean forceReload) {
        if (getView() == null)
            return;

        getEventSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toSortedList()
            .compose(emptiable(getView(), events))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (!forceReload && !events.isEmpty() && isRotated())
            return Observable.fromIterable(events);
        else
            return eventsDataRepository.getEvents(forceReload);
    }

    @VisibleForTesting
    public IEventsView getView() {
        return super.getView();
    }

}
