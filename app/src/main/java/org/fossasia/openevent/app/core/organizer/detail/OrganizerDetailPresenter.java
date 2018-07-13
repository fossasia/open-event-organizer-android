package org.fossasia.openevent.app.core.organizer.detail;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.user.Image;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.user.UserRepositoryImpl;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class OrganizerDetailPresenter extends AbstractBasePresenter<OrganizerDetailView> {

    private final UserRepositoryImpl userRepository;
    private User user;
    private final DatabaseChangeListener<User> userChangeListener;

    @Inject
    public OrganizerDetailPresenter(UserRepositoryImpl userRepository, DatabaseChangeListener<User> userChangeListener) {
        this.userRepository = userRepository;
        this.userChangeListener = userChangeListener;
    }

    @Override
    public void start() {
        loadOrganizer(false);
        listenChanges();
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

    private void listenChanges() {
        userChangeListener.startListening();
        userChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(speakersCallModelChange -> loadOrganizer(false), Logger::logError);
    }

    public void uploadImage(String imageString) {
        Image image = new Image();
        image.setData(imageString);

        userRepository.uploadProfileImage(user, image)
            .compose(progressiveErroneous(getView()))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<User> getOrganizerSource(boolean forceReload) {
        if (user != null && !forceReload && isRotated()) {
            return Observable.just(user);
        } else {
            return userRepository.getOrganizer(forceReload);
        }
    }
}
