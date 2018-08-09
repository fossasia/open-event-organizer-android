package com.eventyay.organizer.core.organizer.detail;

import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepositoryImpl;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class OrganizerDetailPresenter extends AbstractBasePresenter<OrganizerDetailView> {

    private final UserRepositoryImpl userRepository;

    private User user;

    @Inject
    public OrganizerDetailPresenter(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
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
            return userRepository.getOrganizer(forceReload);
        }
    }
}
