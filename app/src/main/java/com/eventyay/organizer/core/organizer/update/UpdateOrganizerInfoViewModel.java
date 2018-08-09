package com.eventyay.organizer.core.organizer.update;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class UpdateOrganizerInfoViewModel extends ViewModel {

    private final UserRepository userRepository;
    private User user = new User();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<User> userLiveData = new SingleEventLiveData<>();

    @Inject
    public UpdateOrganizerInfoViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser() {
        return user;
    }

    @VisibleForTesting
    protected void nullifyEmptyFields(User user) {
        user.setGooglePlusUrl(StringUtils.emptyToNull(user.getGooglePlusUrl()));
        user.setInstagramUrl(StringUtils.emptyToNull(user.getInstagramUrl()));
        user.setFacebookUrl(StringUtils.emptyToNull(user.getFacebookUrl()));
        user.setAvatarUrl(StringUtils.emptyToNull(user.getAvatarUrl()));
        user.setThumbnailImageUrl(StringUtils.emptyToNull(user.getThumbnailImageUrl()));
        user.setTwitterUrl(StringUtils.emptyToNull(user.getTwitterUrl()));
    }

    public void loadUser() {
        compositeDisposable.add(
            userRepository
                .getOrganizer(false)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> {
                    progress.setValue(false);
                    userLiveData.setValue(user);
                }).subscribe(loadedUser -> this.user = loadedUser, Logger::logError));
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void updateOrganizer() {
        nullifyEmptyFields(user);

        compositeDisposable.add(
            userRepository.updateUser(user)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedUser -> {
                    success.setValue("User Updated");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
