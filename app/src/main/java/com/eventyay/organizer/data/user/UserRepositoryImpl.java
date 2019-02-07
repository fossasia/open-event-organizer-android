package com.eventyay.organizer.data.user;

import android.support.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.auth.AuthHolder;
import com.eventyay.organizer.data.event.ImageData;
import com.eventyay.organizer.data.event.ImageUploadApi;
import com.eventyay.organizer.data.event.ImageUrl;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserRepositoryImpl implements UserRepository {

    private final UserApi userApi;
    private final Repository repository;
    private final AuthHolder authHolder;
    private final ImageUploadApi imageUploadApi;

    @Inject
    public UserRepositoryImpl(UserApi userApi, Repository repository, AuthHolder authHolder, ImageUploadApi imageUploadApi) {
        this.userApi = userApi;
        this.repository = repository;
        this.authHolder = authHolder;
        this.imageUploadApi = imageUploadApi;
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
    public Observable<ImageUrl> uploadImage(ImageData imageData) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return imageUploadApi
            .postOriginalImage(imageData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
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
