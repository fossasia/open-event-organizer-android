package org.fossasia.openevent.app.core.event.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.ErrorUtils;
import org.fossasia.openevent.app.utils.service.DateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> events = new MutableLiveData<List<Event>>();
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

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public void sortBy(int criteria) {
        if (criteria == SORTBYNAME)
            Collections.sort(events.getValue(), (e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
        else {
            Collections.sort(events.getValue(), DateService::compareEventDates);
        }
    }

    public void loadUserEvents(boolean forceReload) {
        events.setValue(new ArrayList<>());

        compositeDisposable.add(eventsDataRepository.getEvents(forceReload)
            .toSortedList()
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(newEvents -> {
                events.setValue(newEvents);
                success.setValue(true);
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

}
