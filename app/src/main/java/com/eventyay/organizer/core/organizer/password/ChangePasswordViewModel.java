package com.eventyay.organizer.core.organizer.password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.ChangePassword;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class ChangePasswordViewModel extends ViewModel {

    private final AuthService changePasswordModel;
    private final HostSelectionInterceptor interceptor;
    private final Preferences sharedPreferenceModel;
    private final ChangePassword organizerPasswordObject = new ChangePassword();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> baseUrlLiveData = new SingleEventLiveData<>();

    @Inject
    public ChangePasswordViewModel(
            AuthService changePasswordModel,
            HostSelectionInterceptor interceptor,
            Preferences sharedPreferenceModel) {
        this.changePasswordModel = changePasswordModel;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public ChangePassword getChangePasswordObject() {
        return organizerPasswordObject;
    }

    public void changePasswordRequest(
            String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            error.setValue("Passwords Do Not Match");
            return;
        }

        organizerPasswordObject.setOldPassword(oldPassword);
        organizerPasswordObject.setNewPassword(newPassword);
        organizerPasswordObject.setConfirmNewPassword(confirmPassword);

        compositeDisposable.add(
                changePasswordModel
                        .changePassword(organizerPasswordObject)
                        .doOnSubscribe(disposable -> progress.setValue(true))
                        .doFinally(() -> progress.setValue(false))
                        .subscribe(
                                () -> success.setValue("Password Changed Successfully"),
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
    }

    public void setBaseUrl() {
        String baseUrl =
                sharedPreferenceModel.getString(
                        Constants.SHARED_PREFS_BASE_URL, BuildConfig.DEFAULT_BASE_URL);
        baseUrlLiveData.setValue(baseUrl);
        interceptor.setInterceptor(baseUrl);
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

    public LiveData<String> getBaseUrl() {
        return baseUrlLiveData;
    }
}
