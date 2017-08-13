package org.fossasia.openevent.app.module.organizer.detail;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.repository.EventRepository;
import org.fossasia.openevent.app.module.organizer.detail.contract.IOrganizerDetailPresenter;
import org.fossasia.openevent.app.module.organizer.detail.contract.IOrganizerDetailView;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.result;

public class OrganizerDetailPresenter extends BasePresenter<IOrganizerDetailView> implements IOrganizerDetailPresenter {

    private final EventRepository eventRepository;

    private User user;

    @Inject
    public OrganizerDetailPresenter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void start() {
        getOrganizerSource()
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .subscribe(loadedUser -> this.user = loadedUser, Logger::logError);
    }

    private Observable<User> getOrganizerSource() {
        if (user != null && isRotated()) {
            return Observable.just(user);
        } else {
            return eventRepository.getOrganiser(false);
        }
    }

}
