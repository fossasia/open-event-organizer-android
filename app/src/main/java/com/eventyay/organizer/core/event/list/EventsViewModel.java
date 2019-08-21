package com.eventyay.organizer.core.event.list;

import androidx.lifecycle.LiveData;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
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

    private static final String DEVELOPER_MODE_KEY = "developer_mode";

    private final SingleEventLiveData<List<Event>> events = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Event>> liveEvents = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Event>> pastEvents = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Event>> draftEvents = new SingleEventLiveData<>();

    private final EventRepository eventsDataRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> showDeveloperModeFeatures = new SingleEventLiveData<>();

    private final Preferences sharedPreferenceModel;

    public static final int SORTBYDATE = 0;
    public static final int SORTBYNAME = 1;

    @Inject
    public EventsViewModel(EventRepository eventsDataRepository, Preferences sharedPreferenceModel) {
        this.eventsDataRepository = eventsDataRepository;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public LiveData<List<Event>> getEvents(int position) {
        switch (position) {
            case 0:
                return liveEvents;
            case 1:
                return draftEvents;
            case 2:
                return pastEvents;
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

        boolean isDeveloperModeEnabled = sharedPreferenceModel.getBoolean(
            DEVELOPER_MODE_KEY, false);

        if (isDeveloperModeEnabled)
            showDeveloperModeFeatures.call();

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

    public LiveData<Void> getShowDeveloperModeFeatures() {
        return showDeveloperModeFeatures;
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
