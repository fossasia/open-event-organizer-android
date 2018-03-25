package org.fossasia.openevent.app.common.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.models.Copyright_Table;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.contract.ICopyrightRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CopyrightRepository extends Repository implements ICopyrightRepository {

    @Inject
    public CopyrightRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @NonNull
    @Override
    public Observable<Copyright> createCopyright(Copyright copyright) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService
            .postCopyright(copyright)
            .doOnNext(created -> {
                created.setEvent(copyright.getEvent());
                databaseRepository.save(Copyright.class, created)
                .subscribe();
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Copyright> getCopyright(long eventId, boolean reload) {
        Observable<Copyright> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Copyright.class, Copyright_Table.event_id.eq(eventId)).take(1)
        );

        Observable<Copyright> networkObservable = Observable.defer(() ->
            eventService.getCopyright(eventId)
                .doOnNext(copyright -> databaseRepository
                    .save(Copyright.class, copyright)
                    .subscribe()));

        return new AbstractObservableBuilder<Copyright>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Copyright> updateCopyright(Copyright copyright) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.patchCopyright(copyright.getId(), copyright)
            .doOnNext(updatedCopyright -> databaseRepository
                .update(Copyright.class, updatedCopyright)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Completable deleteCopyright(long id) {
        if (!utilModel.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.deleteCopyright(id)
            .doOnComplete(() -> {
                databaseRepository
                    .delete(Copyright.class, Copyright_Table.id.eq(id))
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
