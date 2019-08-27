package com.eventyay.organizer.core.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;
import com.eventyay.organizer.utils.DateUtils;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class OrganizerViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final RxSharedPreferences sharedPreferences;
    private final ContextManager contextManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<User> organizer = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final SingleEventLiveData<Void> logoutAction = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> localDatePreferenceAction = new SingleEventLiveData<>();

    @Inject
    public OrganizerViewModel(
            UserRepository userRepository,
            AuthService authService,
            RxSharedPreferences sharedPreferences,
            ContextManager contextManager) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.sharedPreferences = sharedPreferences;
        this.contextManager = contextManager;
    }

    protected LiveData<User> getOrganizer() {
        if (organizer.getValue() == null) {
            compositeDisposable.add(
                    userRepository
                            .getOrganizer(false)
                            .subscribe(organizer::setValue, Logger::logError));
        }
        return organizer;
    }

    protected void setLocalDatePreferenceAction() {
        compositeDisposable.add(
                sharedPreferences
                        .getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
                        .asObservable()
                        .distinctUntilChanged()
                        .doOnNext(changed -> localDatePreferenceAction.call())
                        .subscribe(DateUtils::setShowLocal));
    }

    public void logout() {
        compositeDisposable.add(
                authService
                        .logout()
                        .subscribe(
                                () -> {
                                    contextManager.clearOrganiser();
                                    logoutAction.call();
                                },
                                throwable -> {
                                    error.setValue(throwable.getMessage());
                                    Logger.logError(throwable);
                                }));
    }

    protected LiveData<String> getError() {
        return error;
    }

    protected LiveData<Void> getLocalDatePreferenceAction() {
        return localDatePreferenceAction;
    }

    protected LiveData<Void> getLogoutAction() {
        return logoutAction;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
