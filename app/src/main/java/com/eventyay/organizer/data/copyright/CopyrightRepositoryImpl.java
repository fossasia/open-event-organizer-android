package com.eventyay.organizer.data.copyright;

import androidx.annotation.NonNull;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CopyrightRepositoryImpl implements CopyrightRepository {

    private final Repository repository;
    private final CopyrightApi copyrightApi;

    @Inject
    public CopyrightRepositoryImpl(Repository repository, CopyrightApi copyrightApi) {
        this.repository = repository;
        this.copyrightApi = copyrightApi;
    }

    @NonNull
    @Override
    public Observable<Copyright> createCopyright(Copyright copyright) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return copyrightApi
                .postCopyright(copyright)
                .doOnNext(
                        created -> {
                            created.setEvent(copyright.getEvent());
                            repository.save(Copyright.class, created).subscribe();
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Copyright> getCopyright(long eventId, boolean reload) {
        Observable<Copyright> diskObservable =
                Observable.defer(
                        () ->
                                repository
                                        .getItems(
                                                Copyright.class,
                                                Copyright_Table.event_id.eq(eventId))
                                        .take(1));

        Observable<Copyright> networkObservable =
                Observable.defer(
                        () ->
                                copyrightApi
                                        .getCopyright(eventId)
                                        .doOnNext(
                                                copyright ->
                                                        repository
                                                                .save(Copyright.class, copyright)
                                                                .subscribe()));

        return repository
                .observableOf(Copyright.class)
                .reload(reload)
                .withDiskObservable(diskObservable)
                .withNetworkObservable(networkObservable)
                .build();
    }

    @NonNull
    @Override
    public Observable<Copyright> updateCopyright(Copyright copyright) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return copyrightApi
                .patchCopyright(copyright.getId(), copyright)
                .doOnNext(
                        updatedCopyright ->
                                repository.update(Copyright.class, updatedCopyright).subscribe())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Completable deleteCopyright(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return copyrightApi
                .deleteCopyright(id)
                .doOnComplete(
                        () ->
                                repository
                                        .delete(Copyright.class, Copyright_Table.id.eq(id))
                                        .subscribe())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
