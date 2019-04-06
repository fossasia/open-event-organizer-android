package com.eventyay.organizer.core.event.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.service.DateService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> liveEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> pastEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> draftEvents = new MutableLiveData<>();

    private final EventRepository eventsDataRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public static final int SORTBYDATE = 0;
    public static final int SORTBYNAME = 1;

    @Inject
    public EventsViewModel(EventRepository eventsDataRepository) {
        this.eventsDataRepository = eventsDataRepository;
    }

    public LiveData<List<Event>> getEvents(int position) {
        switch (position) {
            case 0:
                return liveEvents;
            case 1:
                return pastEvents;
            case 2:
                return draftEvents;
            default:
                return events;
        }
    }

    public void sortBy(int criteria) {
        if (events.getValue() == null) {
            return;
        }
        if (criteria == SORTBYNAME)
            Collections.sort(events.getValue(), (e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
        else {
            Collections.sort(events.getValue(), DateService::compareEventDates);
        }
        filter();
    }

    public void loadUserEvents(boolean forceReload) {

        compositeDisposable.add(eventsDataRepository.getEvents(forceReload)
            .toSortedList()
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(newEvents -> {
                events.setValue(newEvents);
                success.setValue(true);
                filter();
            }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public void filter() {
        List<Event> live = new ArrayList<>();
        List<Event> past = new ArrayList<>();
        List<Event> draft = new ArrayList<>();

        for (Event event : events.getValue()) {
            try {
                if (event.getState().equals("draft"))
                    draft.add(event);
                else if ("past".equalsIgnoreCase(DateService.getEventStatus(event)))
                    past.add(event);
                else
                    live.add(event);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        liveEvents.setValue(live);
        pastEvents.setValue(past);
        draftEvents.setValue(draft);
    }
}
