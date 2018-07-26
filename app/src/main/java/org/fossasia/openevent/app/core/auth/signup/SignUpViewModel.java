package org.fossasia.openevent.app.core.auth.signup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.livedata.SingleEventLiveData;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;


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
