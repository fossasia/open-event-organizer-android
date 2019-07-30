package com.eventyay.organizer.core.event.about;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.Utils;
import com.raizlabs.android.dbflow.structure.BaseModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AboutEventViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final CopyrightRepository copyrightRepository;
    private final DatabaseChangeListener<Copyright> copyrightChangeListener;
    private Event event;
    @Nullable
    private Copyright copyright;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<Event> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Copyright> showCopyright = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> changeCopyrightMenuItem = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> showCopyrightDeleted = new SingleEventLiveData<>();

    private long eventId;

    @Inject
    public AboutEventViewModel(EventRepository eventRepository, CopyrightRepository copyrightRepository,
                               DatabaseChangeListener<Copyright> copyrightChangeListener) {
        this.eventRepository = eventRepository;
        this.copyrightRepository = copyrightRepository;
        this.copyrightChangeListener = copyrightChangeListener;

        eventId = ContextManager.getSelectedEvent().getId();
    }

    public Event getEvent() {
        return event;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Event> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Copyright> getShowCopyright() {
        return showCopyright;
    }

    public LiveData<Boolean> getChangeCopyrightMenuItem() {
        return changeCopyrightMenuItem;
    }

    public LiveData<String> getShowCopyrightDeleted() {
        return showCopyrightDeleted;
    }

    public void loadEvent(boolean forceReload) {
        compositeDisposable.add(
            getEventSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(loadedEvent -> {
                    this.event = loadedEvent;
                    success.setValue(event);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (event != null && !forceReload) {
            return Observable.just(event);
        } else {
            return eventRepository.getEvent(eventId, forceReload);
        }
    }

    public void loadCopyright(boolean forceReload) {
        listenChanges();

        compositeDisposable.add(
            getCopyrightSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .doFinally(this::showCopyright)
                .subscribe(loadedCopyright -> this.copyright = loadedCopyright,
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private void showCopyright() {
        showCopyright.setValue(copyright);
        if (copyright == null) {
            changeCopyrightMenuItem.setValue(true);
        } else {
            changeCopyrightMenuItem.setValue(false);
        }
    }

    private Observable<Copyright> getCopyrightSource(boolean forceReload) {
        if (copyright != null && !forceReload) {
            return Observable.just(copyright);
        } else {
            return copyrightRepository.getCopyright(eventId, forceReload);
        }
    }

    @Nullable
    public Copyright getCopyright() {
        return copyright;
    }

    @SuppressWarnings("PMD.NullAssignment")
    public void deleteCopyright(long id) {
        compositeDisposable.add(
            copyrightRepository
                .deleteCopyright(id)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(() -> {
                    showCopyrightDeleted.setValue("Copyright Deleted");
                    copyright = null;
                    loadCopyright(true);
                }, Logger::logError));
    }

    private void listenChanges() {
        copyrightChangeListener.startListening();
        copyrightChangeListener.getNotifier()
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(copyrightModelChange -> loadCopyright(false), Logger::logError);
    }

    public String getShareableInformation() {
        return Utils.getShareableInformation(event, OrgaProvider.context);
    }
}
