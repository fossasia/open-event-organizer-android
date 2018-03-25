package org.fossasia.openevent.app.module.event.about;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.ICopyrightRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousResultRefresh;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveRefresh;

public class AboutEventPresenter extends BaseDetailPresenter<Long, IAboutEventVew> implements IAboutEventPresenter {

    private final IEventRepository eventRepository;
    private final ICopyrightRepository copyrightRepository;
    private final IDatabaseChangeListener<Copyright> copyrightChangeListener;
    private Event event;
    private Copyright copyright;

    @Inject
    public AboutEventPresenter(IEventRepository eventRepository, ICopyrightRepository copyrightRepository,
                               IDatabaseChangeListener<Copyright> copyrightChangeListener) {
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

    @Override
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

    @Override
    public void loadCopyright(boolean forceReload) {
        getCopyrightSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveRefresh(getView(), forceReload))
            .doFinally(this::showCopyright)
            .subscribe(loadedCopyright -> this.copyright = loadedCopyright, Logger::logError);
    }

    private void showCopyright() {
        getView().showCopyright(copyright);
        if (copyright != null) {
            getView().changeCopyrightMenuItem();
        }
    }

    private Observable<Copyright> getCopyrightSource(boolean forceReload) {
        if (copyright != null && !forceReload && isRotated()) {
            return Observable.just(copyright);
        } else {
            return copyrightRepository.getCopyright(getId(), forceReload);
        }
    }

    @Override
    public Copyright getCopyright() {
        return copyright;
    }

    private void listenChanges() {
        copyrightChangeListener.startListening();
        copyrightChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(copyrightModelChange -> loadCopyright(false), Logger::logError);
    }

    @Override
    public void detach() {
        super.detach();
        copyrightChangeListener.stopListening();
    }
}
