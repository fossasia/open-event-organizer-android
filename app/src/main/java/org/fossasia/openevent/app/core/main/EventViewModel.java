package org.fossasia.openevent.app.core.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.livedata.SingleEventLiveData;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.CurrencyUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.core.main.MainActivity.EVENT_KEY;

public class EventViewModel extends ViewModel {
    private final Preferences sharedPreferenceModel;
    private boolean initialized = false;
    private final Bus bus;
    private final CurrencyUtils currencyUtils;
    private final EventRepository eventRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Long> eventId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();
    private final SingleEventLiveData<Void> showEventList = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> showDashboard = new SingleEventLiveData<>();

    @Inject
    public EventViewModel(Preferences sharedPreferenceModel, Bus bus,
                          CurrencyUtils currencyUtils, EventRepository eventRepository) {
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.bus = bus;
        this.currencyUtils = currencyUtils;
        this.eventRepository = eventRepository;
    }

    protected void onStart() {
        if (initialized)
            return;

        initialized = true;
        compositeDisposable.add(bus.getSelectedEvent()
            .subscribe(event -> {
                sharedPreferenceModel.setLong(EVENT_KEY, event.getId());
                ContextManager.setSelectedEvent(event);
                currencyUtils.getCurrencySymbol(event.getPaymentCurrency())
                    .subscribe(ContextManager::setCurrency, Logger::logError);
                showLoadedEvent(event.getId());
            }, throwable -> {
                Logger.logError(throwable);
                error.setValue(throwable.getMessage());
            }));

        long storedEventId = sharedPreferenceModel.getLong(EVENT_KEY, -1);

        if (storedEventId == -1)
            showEventList();
        else
            showLoadedEvent(storedEventId);
    }

    private void showLoadedEvent(long storedEventId) {
        eventId.setValue(storedEventId);
        Event staticEvent = ContextManager.getSelectedEvent();

        if (staticEvent != null) {
            selectedEvent.setValue(staticEvent);
            showEventDashboard();
            return;
        }

        compositeDisposable.add(eventRepository
            .getEvent(storedEventId, false)
            .subscribe(bus::pushSelectedEvent, throwable -> {
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
}
