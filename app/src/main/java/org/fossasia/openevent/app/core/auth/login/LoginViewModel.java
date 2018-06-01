package org.fossasia.openevent.app.core.auth.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.utils.ErrorUtils;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class LoginViewModel extends ViewModel {

    private final AuthService loginModel;
    private final Login login = new Login();
    private final HostSelectionInterceptor interceptor;
    private final Preferences sharedPreferenceModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn;
    private MutableLiveData<Set<String>> emailList;

    @Inject
    public LoginViewModel(AuthService loginModel, HostSelectionInterceptor interceptor, Preferences sharedPreferenceModel) {
        this.loginModel = loginModel;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    //for logging into the app
    public void login() {
        compositeDisposable.add(loginModel.login(login)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> isLoggedIn.setValue(true),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable))));
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getLoginStatus() {
        if (isLoggedIn == null)
            isLoggedIn = new MutableLiveData<>();

        boolean loginValue = loginModel.isLoggedIn();

        if (loginValue) {
            isLoggedIn.setValue(true);
        }
        return isLoggedIn;
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    public Login getLogin() {
        return login;
    }

    //fetching the email list from the shared preferences
    public LiveData<Set<String>> getEmailList() {
        if (emailList == null)
            emailList = new MutableLiveData<>();

        Set<String> emailSet = sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);

        if (emailSet != null) {
            emailList.setValue(emailSet);
        }
        return emailList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
