package org.fossasia.openevent.app.core.organizer.update;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.user.UserRepository;
import org.fossasia.openevent.app.data.user.UserRepositoryImpl;
import org.fossasia.openevent.app.utils.StringUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class UpdateOrganizerInfoPresenter extends AbstractBasePresenter<UpdateOrganizerInfoView> {

    private final UserRepository userRepository;
    private User user;

    @Inject
    public UpdateOrganizerInfoPresenter(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public User getUser() {
        return user;
    }

    protected void nullifyEmptyFields(User user) {
        user.setGooglePlusUrl(StringUtils.emptyToNull(user.getGooglePlusUrl()));
        user.setInstagramUrl(StringUtils.emptyToNull(user.getInstagramUrl()));
        user.setFacebookUrl(StringUtils.emptyToNull(user.getFacebookUrl()));
        user.setAvatarUrl(StringUtils.emptyToNull(user.getAvatarUrl()));
        user.setThumbnailImageUrl(StringUtils.emptyToNull(user.getThumbnailImageUrl()));
        user.setTwitterUrl(StringUtils.emptyToNull(user.getTwitterUrl()));
    }

    public void loadUser() {
        userRepository
            .getOrganizer(false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showUser)
            .subscribe(loadedUser -> this.user = loadedUser, Logger::logError);
    }

    private void showUser() {
        getView().setUser(user);
    }

    public void updateOrganizer() {
        nullifyEmptyFields(user);

        userRepository.updateUser(user)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedUser -> {
                getView().onSuccess("User Updated");
                getView().dismiss();
            }, Logger::logError);

    }
}
