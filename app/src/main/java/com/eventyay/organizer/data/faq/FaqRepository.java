package com.eventyay.organizer.data.faq;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface FaqRepository {

    Observable<List<Faq>> getFaqs(long id, boolean reload);

    Completable createFaq(Faq faq);

    Completable deleteFaq(long id);
}
