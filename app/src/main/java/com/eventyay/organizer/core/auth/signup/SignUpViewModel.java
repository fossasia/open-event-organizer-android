package com.eventyay.organizer.core.auth.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;


public class SignUpViewModel extends ViewModel {

    private final AuthService authModel;
    private final HostSelectionInterceptor interceptor;
    private final User user = new User();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();

    @Inject
    public SignUpViewModel(AuthService authModel, HostSelectionInterceptor interceptor) {
        this.authModel = authModel;
        this.interceptor = interceptor;
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

    public void signUp() {
        compositeDisposable.add(
            authModel.signUp(user)
                .compose(dispose(compositeDisposable))
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(user -> {
                        success.setValue("Successfully Registered");
                    },
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    public boolean arePasswordsEqual(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            error.setValue("Passwords don't match!");
            return false;
        }
        return true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
