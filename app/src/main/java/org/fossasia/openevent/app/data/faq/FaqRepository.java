package org.fossasia.openevent.app.data.faq;

import io.reactivex.Observable;

public interface FaqRepository {

    Observable<Faq> getFaqs(long id, boolean reload);

    Observable<Faq> createFaq(Faq faq);

}
