package org.fossasia.openevent.app.core.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.user.UserRepository;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;

public class OrganizerViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<User> organizer = new MutableLiveData<>();

    @Inject
    public OrganizerViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected LiveData<User> getOrganizer() {
        if (organizer.getValue() == null) {
            compositeDisposable.add(userRepository
                .getOrganizer(false)
                .compose(dispose(compositeDisposable))
                .subscribe(organizer::setValue, Logger::logError));
        }
        return organizer;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
