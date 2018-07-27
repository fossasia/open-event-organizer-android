package com.eventyay.organizer.core.organizer.update;

import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;
import com.eventyay.organizer.data.user.UserRepositoryImpl;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

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
