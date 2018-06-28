package org.fossasia.openevent.app.core.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.livedata.SingleEventLiveData;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.user.UserRepository;
import org.fossasia.openevent.app.utils.DateUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

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
    public OrganizerViewModel(UserRepository userRepository, AuthService authService,
                              RxSharedPreferences sharedPreferences, ContextManager contextManager) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.sharedPreferences = sharedPreferences;
        this.contextManager = contextManager;
    }

    protected LiveData<User> getOrganizer() {
        if (organizer.getValue() == null) {
            compositeDisposable.add(userRepository
                .getOrganizer(false)
                .subscribe(organizer::setValue, Logger::logError));
        }
        return organizer;
    }

    protected void setLocalDatePreferenceAction() {
        compositeDisposable.add(sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
            .asObservable()
            .distinctUntilChanged()
            .doOnNext(changed -> localDatePreferenceAction.call())
            .subscribe(DateUtils::setShowLocal));
    }

    public void logout() {
        compositeDisposable.add(authService.logout()
            .subscribe(() -> {
                contextManager.clearOrganiser();
                logoutAction.call();
            }, throwable -> {
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
