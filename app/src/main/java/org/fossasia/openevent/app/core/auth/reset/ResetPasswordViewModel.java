package org.fossasia.openevent.app.core.auth.reset;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.auth.model.RequestToken;
import org.fossasia.openevent.app.data.auth.model.SubmitToken;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ResetPasswordViewModel extends ViewModel {

    private final AuthService tokenSubmitModel;
    private final HostSelectionInterceptor interceptor;
    private final SubmitToken submitToken = new SubmitToken();
    private final RequestToken requestToken = new RequestToken();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    @Inject
    public ResetPasswordViewModel(AuthService tokenSubmitModel,
                                  HostSelectionInterceptor interceptor) {
        this.tokenSubmitModel = tokenSubmitModel;
        this.interceptor = interceptor;
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

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
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

}
