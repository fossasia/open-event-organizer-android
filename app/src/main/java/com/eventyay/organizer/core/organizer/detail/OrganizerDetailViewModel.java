package com.eventyay.organizer.core.organizer.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.ResendVerificationMail;
import com.eventyay.organizer.data.auth.model.SubmitEmailVerificationToken;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class OrganizerDetailViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final ResendVerificationMail resendVerificationMail = new ResendVerificationMail();
    private final SubmitEmailVerificationToken submitEmailVerificationToken = new SubmitEmailVerificationToken();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private User user;

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<User> userLiveData = new SingleEventLiveData<>();

    @Inject
    public OrganizerDetailViewModel(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public User getUser() {
        return user;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setToken(String token) {
        submitEmailVerificationToken.setToken(token);
    }

    public void loadOrganizer(boolean forceReload) {
        compositeDisposable.add(
            getOrganizerSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(loadedUser -> {
                    this.user = loadedUser;
                    resendVerificationMail.setEmail(user.getEmail());
                    success.setValue("Organizer Details Loaded Successfully");
                    userLiveData.setValue(user);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void resendVerificationMail() {
        compositeDisposable.add(
            authService.resendVerificationMail(resendVerificationMail)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(resendMailResponse -> success.setValue("Verification Mail Resent"),
                    throwable -> {
                        error.setValue(ErrorUtils.getErrorDetails(throwable).toString());
                        Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
                    }));
    }

    public void verifyMail() {
        compositeDisposable.add(
            authService.verifyMail(submitEmailVerificationToken)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(() -> success.setValue("Mail Verified"),
                    throwable -> {
                        error.setValue(ErrorUtils.getErrorDetails(throwable).toString());
                        Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
                    }));
    }

    //Method for storing user uploaded image in temporary location
    public void uploadImage(ImageData imageData) {
        compositeDisposable.add(
            userRepository
                .uploadOrganizerImage(imageData)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(uploadedImage -> {
                    success.setValue("Image Uploaded Successfully");
                    Timber.e(uploadedImage.getUrl());
                    user.setAvatarUrl(uploadedImage.getUrl());
                    updateOrganizer();
                }, throwable -> error.setValue(ErrorUtils.getErrorDetails(throwable).toString())));
    }

    public void updateOrganizer() {
        compositeDisposable.add(
            userRepository.updateUser(user)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedUser -> {
                    success.setValue("User Updated");
                    loadOrganizer(false);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<User> getOrganizerSource(boolean forceReload) {
        if (user != null && !forceReload) {
            return Observable.just(user);
        } else {
            return userRepository.getOrganizer(forceReload);
        }
    }
}
