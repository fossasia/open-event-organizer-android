package org.fossasia.openevent.app.data.user;

import androidx.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.auth.AuthHolder;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserRepositoryImpl implements UserRepository {


    private final UserApi userApi;
    private final Repository repository;
    private final AuthHolder authHolder;

    @Inject
    public UserRepositoryImpl(UserApi userApi, Repository repository, AuthHolder authHolder) {
        this.userApi = userApi;
        this.repository = repository;
        this.authHolder = authHolder;
    }

    @Override
    public Observable<User> getOrganizer(boolean reload) {
        int userId = authHolder.getIdentity();
        Observable<User> diskObservable = Observable.defer(() ->
            repository.getItems(User.class, User_Table.id.eq(userId))
        );

        Observable<User> networkObservable = Observable.defer(() ->
            userApi
                .getOrganizer(userId)
                .doOnNext(user -> repository
                    .save(User.class, user)
                    .subscribe()));

        return repository.observableOf(User.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<User> updateUser(User user) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }
        return userApi.patchUser(user.getId(), user)
            .doOnNext(updatedUser -> repository
                .update(User.class, updatedUser)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
