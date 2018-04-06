package org.fossasia.openevent.app.data.repository;

import org.fossasia.openevent.app.data.models.Faq;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IFaqRepository {

    Observable<Faq> getFaqs(long id, boolean reload);

    Observable<Faq> createFaq(Faq faq);

    Completable deleteFaq(long id);
}
