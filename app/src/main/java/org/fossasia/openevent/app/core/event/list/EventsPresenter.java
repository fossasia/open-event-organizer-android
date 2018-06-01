package org.fossasia.openevent.app.core.event.list;

import android.databinding.ObservableBoolean;
import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.service.DateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class EventsPresenter extends AbstractBasePresenter<EventsView> {

    private final List<Event> events = new ArrayList<>();
    private boolean editMode;
    private final EventRepository eventsDataRepository;
    private Event lastEvent = new Event();

    public final Map<Event, ObservableBoolean> selectedMap = new ConcurrentHashMap<>();
    public static final int SORTBYDATE = 0;
    public static final int SORTBYNAME = 1;

    @Inject
    public EventsPresenter(EventRepository eventsDataRepository) {
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
    public EventsView getView() {
        return super.getView();
    }

    public void unselectEvent(Event event) {
        if (event != null && selectedMap.containsKey(event))
            selectedMap.get(event).set(false);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    public void toolbarEditMode(Event currentEvent) {
        long id = 0;
        if (!lastEvent.equals(currentEvent)) {
            unselectEvent(lastEvent);
            id = currentEvent.getId();
        }
        selectedMap.get(currentEvent).set(true);
        editMode = true;
        lastEvent = currentEvent;
        getView().changeToEditMode(id);
    }

    public void resetToDefaultState() {
        unselectEvent(lastEvent);
        editMode = false;
        getView().changeToNormalMode();
    }

    public boolean isEditMode() {
        return editMode;
    }

    public ObservableBoolean getEventsSelected(Event event) {
        if (!selectedMap.containsKey(event)) {
            selectedMap.put(event, new ObservableBoolean(false));
        }
        return selectedMap.get(event);
    }

    @Override
    public void detach() {
        super.detach();
        selectedMap.clear();
        resetToDefaultState();
    }
}
