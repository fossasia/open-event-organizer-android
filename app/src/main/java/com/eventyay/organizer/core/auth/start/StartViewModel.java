package com.eventyay.organizer.core.auth.start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.EmailRequest;
import com.eventyay.organizer.data.encryption.EncryptionService;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.Constants.PREF_USER_EMAIL;

public class StartViewModel extends ViewModel {

    private final AuthService authService;
    private final HostSelectionInterceptor interceptor;
    private final EncryptionService encryptionService;
    private final Preferences sharedPreferenceModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> emailList = new MutableLiveData<>();;
    private final SingleEventLiveData<Boolean> isEmailRegistered = new SingleEventLiveData<>();
    private final MutableLiveData<EmailRequest> emailRequestModel = new MutableLiveData<>();

    @Inject
    public StartViewModel(AuthService authService, HostSelectionInterceptor interceptor,
                          Preferences sharedPreferenceModel, EncryptionService encryptionService) {
        this.authService = authService;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.encryptionService = encryptionService;
    }

    public void checkIsEmailRegistered(EmailRequest emailRequest) {
        compositeDisposable.add(authService.checkEmailRegistered(emailRequest)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe((isAvailable) -> {
                    isEmailRegistered.setValue(!isAvailable.getResult());
                },
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public MutableLiveData<EmailRequest> getEmailRequestModel() {
        if (emailRequestModel.getValue() == null) {
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setEmail(encryptionService.decrypt(sharedPreferenceModel.getString(PREF_USER_EMAIL, null)));
            emailRequestModel.setValue(emailRequest);
        }
        return emailRequestModel;
    }

    public LiveData<Boolean> getLoginStatus() {
        boolean loginValue = authService.isLoggedIn();

        if (loginValue) {
            isLoggedIn.setValue(true);
        }
        return isLoggedIn;
    }

    public LiveData<Set<String>> getEmailList() {
        Set<String> emailSet = sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);

        if (emailSet != null) {
            emailList.setValue(emailSet);
        }
        return emailList;
    }

    public LiveData<Boolean> getIsEmailRegistered() {
        return isEmailRegistered;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
        sharedPreferenceModel.saveString(Constants.SHARED_PREFS_BASE_URL, baseUrl);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
