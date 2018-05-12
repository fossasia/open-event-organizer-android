package org.fossasia.openevent.app.data.sponsor;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SponsorRepositoryImpl implements SponsorRepository {

    private final Repository repository;
    private final SponsorApi sponsorApi;

    @Inject
    public SponsorRepositoryImpl(Repository repository, SponsorApi sponsorApi) {
        this.repository = repository;
        this.sponsorApi = sponsorApi;
    }

    @NonNull
    @Override
    public Observable<Sponsor> getSponsors(long eventId, boolean reload) {
        Observable<Sponsor> diskObservable = Observable.defer(() ->
            repository.getItems(Sponsor.class, Sponsor_Table.event_id.eq(eventId))
        );

        Observable<Sponsor> networkObservable = Observable.defer(() ->
            sponsorApi.getSponsors(eventId)
                .doOnNext(sponsors -> repository
                    .syncSave(Sponsor.class, sponsors, Sponsor::getId, Sponsor_Table.id)
                    .subscribe())
                .flatMapIterable(sponsors -> sponsors));

        return repository.observableOf(Sponsor.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Sponsor> createSponsor(Sponsor sponsor) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sponsorApi
            .postSponsor(sponsor)
            .doOnNext(created -> {
                created.setEvent(sponsor.getEvent());
                repository
                    .save(Sponsor.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
