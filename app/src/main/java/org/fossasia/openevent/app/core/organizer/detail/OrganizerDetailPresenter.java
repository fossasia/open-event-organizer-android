package org.fossasia.openevent.app.core.organizer.detail;

import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.repository.EventRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class OrganizerDetailPresenter extends BasePresenter<IOrganizerDetailView> {

    private final EventRepository eventRepository;

    private User user;

    @Inject
    public OrganizerDetailPresenter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void start() {
        loadOrganizer(false);
    }

    public void loadOrganizer(boolean forceReload) {
        getOrganizerSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .subscribe(loadedUser -> {
                this.user = loadedUser;
                getView().showResult(user);
            }, Logger::logError);
    }

    private Observable<User> getOrganizerSource(boolean forceReload) {
        if (user != null && !forceReload && isRotated()) {
            return Observable.just(user);
        } else {
            return eventRepository.getOrganiser(forceReload);
        }
    }
}
