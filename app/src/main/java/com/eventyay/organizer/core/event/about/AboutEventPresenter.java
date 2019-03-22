package com.eventyay.organizer.core.event.about;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.structure.BaseModel;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousResultRefresh;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveRefresh;

public class AboutEventPresenter extends AbstractDetailPresenter<Long, AboutEventView> {

    private final EventRepository eventRepository;
    private final CopyrightRepository copyrightRepository;
    private final DatabaseChangeListener<Copyright> copyrightChangeListener;
    private Event event;
    @Nullable
    private Copyright copyright;

    @Inject
    public AboutEventPresenter(EventRepository eventRepository, CopyrightRepository copyrightRepository,
                               DatabaseChangeListener<Copyright> copyrightChangeListener) {
        this.eventRepository = eventRepository;
        this.copyrightRepository = copyrightRepository;
        this.copyrightChangeListener = copyrightChangeListener;
    }

    @Override
    public void start() {
        loadEvent(false);
        loadCopyright(false);
        listenChanges();
    }

    public void loadEvent(boolean forceReload) {
        getEventSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousResultRefresh(getView(), forceReload))
            .subscribe(loadedEvent -> this.event = loadedEvent, Logger::logError);
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (event != null && !forceReload && isRotated()) {
            return Observable.just(event);
        } else {
            return eventRepository.getEvent(getId(), forceReload);
        }
    }

    public void loadCopyright(boolean forceReload) {
        getCopyrightSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveRefresh(getView(), forceReload))
            .doFinally(this::showCopyright)
            .subscribe(loadedCopyright -> this.copyright = loadedCopyright, Logger::logError);
    }

    private void showCopyright() {
        getView().showCopyright(copyright);
        if (copyright == null) {
            getView().changeCopyrightMenuItem(true);
        } else {
            getView().changeCopyrightMenuItem(false);
        }
    }

    private Observable<Copyright> getCopyrightSource(boolean forceReload) {
        if (copyright != null && !forceReload && isRotated()) {
            return Observable.just(copyright);
        } else {
            return copyrightRepository.getCopyright(getId(), forceReload);
        }
    }

    @Nullable
    public Copyright getCopyright() {
        return copyright;
    }

    @SuppressWarnings("PMD.NullAssignment")
    public void deleteCopyright(long id) {
        copyrightRepository
            .deleteCopyright(id)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> {
                getView().showCopyrightDeleted("Copyright Deleted");
                copyright = null;
                loadCopyright(true);
            }, Logger::logError);
    }

    private void listenChanges() {
        copyrightChangeListener.startListening();
        copyrightChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(copyrightModelChange -> loadCopyright(false), Logger::logError);
    }

    public String getShareableInformation() {
        return Utils.getShareableInformation(event);
    }

    @Override
    public void detach() {
        super.detach();
        copyrightChangeListener.stopListening();
    }
}
