package com.eventyay.organizer.core.auth.reset;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.auth.model.SubmitToken;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ResetPasswordViewModel extends ViewModel {

    private final AuthService tokenSubmitModel;
    private final HostSelectionInterceptor interceptor;
    private final Preferences sharedPreferenceModel;
    private final SubmitToken submitToken = new SubmitToken();
    private final RequestToken requestToken = new RequestToken();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> message = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> baseUrlLiveData = new SingleEventLiveData<>();

    @Inject
    public ResetPasswordViewModel(AuthService tokenSubmitModel,
                                  HostSelectionInterceptor interceptor,
                                  Preferences sharedPreferenceModel) {
        this.tokenSubmitModel = tokenSubmitModel;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public SubmitToken getSubmitToken() {
        return submitToken;
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public void submitRequest(SubmitToken token) {
        compositeDisposable.add(tokenSubmitModel.submitToken(token)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> success.setValue("Password Changed Successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void setBaseUrl() {
        String baseUrl = sharedPreferenceModel.getString(Constants.SHARED_PREFS_BASE_URL,
            BuildConfig.DEFAULT_BASE_URL);
        baseUrlLiveData.setValue(baseUrl);
        interceptor.setInterceptor(baseUrl);
    }

    public void requestToken(String email) {
        getRequestToken().setEmail(email);

        compositeDisposable.add(tokenSubmitModel.requestToken(requestToken)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> message.setValue("Token sent successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void setToken(String token) {
        submitToken.setToken(token);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getBaseUrl() {
        return baseUrlLiveData;
    }
}
