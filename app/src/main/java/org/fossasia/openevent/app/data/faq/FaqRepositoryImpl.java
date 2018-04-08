package org.fossasia.openevent.app.data.faq;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FaqRepositoryImpl implements FaqRepository {

    private final FaqApi faqApi;
    private final Repository repository;

    @Inject
    public FaqRepositoryImpl(FaqApi faqApi, Repository repository) {
        this.faqApi = faqApi;
        this.repository = repository;
    }

    @Override
    public Observable<Faq> getFaqs(long eventId, boolean reload) {
        Observable<Faq> diskObservable = Observable.defer(() ->
            repository.getItems(Faq.class, Faq_Table.event_id.eq(eventId))
        );

        Observable<Faq> networkObservable = Observable.defer(() ->
            faqApi.getFaqs(eventId)
                .doOnNext(faqs -> repository.syncSave(Faq.class, faqs, Faq::getId, Faq_Table.id).subscribe())
                .flatMapIterable(faqs -> faqs));

        return repository.observableOf(Faq.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Faq> createFaq(Faq faq) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return faqApi
            .postFaq(faq)
            .doOnNext(created -> {
                created.setEvent(faq.getEvent());
                repository
                    .save(Faq.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Completable deleteFaq(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return faqApi.deleteFaq(id)
            .doOnComplete(() -> repository
                .delete(Faq.class, Faq_Table.id.eq(id))
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
