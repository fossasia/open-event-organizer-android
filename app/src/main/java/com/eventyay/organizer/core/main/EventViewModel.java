package com.eventyay.organizer.core.main;

import static com.eventyay.organizer.core.main.MainActivity.EVENT_KEY;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Bus;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.CurrencyUtils;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class EventViewModel extends ViewModel {
    private final Preferences sharedPreferenceModel;
    private final Bus bus;
    private final CurrencyUtils currencyUtils;
    private final EventRepository eventRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Long> eventId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();
    private final SingleEventLiveData<Void> showEventList = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> showDashboard = new SingleEventLiveData<>();

    private boolean initialized;

    @Inject
    public EventViewModel(
            Preferences sharedPreferenceModel,
            Bus bus,
            CurrencyUtils currencyUtils,
            EventRepository eventRepository) {
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.bus = bus;
        this.currencyUtils = currencyUtils;
        this.eventRepository = eventRepository;
    }

    protected void onStart() {
        if (initialized) return;

        initialized = true;
        compositeDisposable.add(
                bus.getSelectedEvent()
                        .subscribe(
                                event -> {
                                    sharedPreferenceModel.setLong(EVENT_KEY, event.getId());
                                    ContextManager.setSelectedEvent(event);
                                    currencyUtils
                                            .getCurrencySymbol(event.getPaymentCurrency())
                                            .subscribe(
                                                    ContextManager::setCurrency, Logger::logError);
                                    showLoadedEvent(event.getId());
                                },
                                throwable -> {
                                    Logger.logError(throwable);
                                    error.setValue(throwable.getMessage());
                                }));

        long storedEventId = sharedPreferenceModel.getLong(EVENT_KEY, -1);

        if (storedEventId == -1) showEventList();
        else showLoadedEvent(storedEventId);
    }

    private void showLoadedEvent(long storedEventId) {
        eventId.setValue(storedEventId);
        Event staticEvent = ContextManager.getSelectedEvent();

        if (staticEvent != null) {
            selectedEvent.setValue(staticEvent);
            showEventDashboard();
            return;
        }

        compositeDisposable.add(
                eventRepository
                        .getEvent(storedEventId, false)
                        .subscribe(
                                bus::pushSelectedEvent,
                                throwable -> {
                                    Logger.logError(throwable);
                                    error.setValue(throwable.getMessage());
                                }));
    }

    private void showEventList() {
        showEventList.call();
    }

    private void showEventDashboard() {
        showDashboard.call();
    }

    protected LiveData<Long> getEventId() {
        return eventId;
    }

    protected LiveData<String> getError() {
        return error;
    }

    protected LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    protected LiveData<Void> getShowEventList() {
        return showEventList;
    }

    protected LiveData<Void> getShowDashboard() {
        return showDashboard;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void unselectEvent() {
        sharedPreferenceModel.setLong(EVENT_KEY, -1);
    }
}
