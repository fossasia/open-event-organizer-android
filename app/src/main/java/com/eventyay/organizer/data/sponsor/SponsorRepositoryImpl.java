package com.eventyay.organizer.data.sponsor;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;

import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SponsorRepositoryImpl implements SponsorRepository {

    private final Repository repository;
    private final SponsorApi sponsorApi;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

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
            .withRateLimiterConfig("Sponsors", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Sponsor> getSponsor(long sponsorId, boolean reload) {
        Observable<Sponsor> diskObservable = Observable.defer(() ->
            repository
                .getItems(Sponsor.class, Sponsor_Table.id.eq(sponsorId)).take(1)
        );

        Observable<Sponsor> networkObservable = Observable.defer(() ->
            sponsorApi.getSponsor(sponsorId)
                .doOnNext(sponsor -> repository
                    .save(Sponsor.class, sponsor)
                    .subscribe()));

        return repository
            .observableOf(Sponsor.class)
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

    @NonNull
    @Override
    public Observable<Sponsor> updateSponsor(Sponsor sponsor) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sponsorApi
            .updateSponsor(sponsor.getId(), sponsor)
            .doOnNext(updatedSponsor -> repository
                .update(Sponsor.class, updatedSponsor)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deleteSponsor(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sponsorApi.deleteSponsor(id)
            .doOnComplete(() -> repository
                .delete(Sponsor.class, Sponsor_Table.id.eq(id))
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
