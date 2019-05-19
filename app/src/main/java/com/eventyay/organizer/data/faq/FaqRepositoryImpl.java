package com.eventyay.organizer.data.faq;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FaqRepositoryImpl implements FaqRepository {

    private final FaqApi faqApi;
    private final Repository repository;
    private FaqDao faqDao;

    @Inject
    public FaqRepositoryImpl(FaqApi faqApi, Repository repository, FaqDao faqDao) {
        this.faqApi = faqApi;
        this.repository = repository;
        this.faqDao = faqDao;
    }

    @Override
    public Observable<List<Faq>> getFaqs(long eventId, boolean reload) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        if (!reload) {
            return faqDao.getAllFaqs(eventId)
                .switchIfEmpty(faqApi.getFaqs(eventId)
                    .doOnNext(faqList -> faqDao.insertFaqList(faqList).subscribe()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        } else {
            return faqApi.getFaqs(eventId)
                .doOnNext(faqList -> faqDao.insertFaqList(faqList).subscribe())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        }
    }

    @NonNull
    @Override
    public Completable createFaq(Faq faq) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return faqApi
            .postFaq(faq)
            .flatMapCompletable(createdFaq -> {
                createdFaq.setEvent(faq.getEvent());
                return faqDao.insertFaq(createdFaq);
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
            .doOnComplete(() -> faqDao.deleteFaq(id).subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
