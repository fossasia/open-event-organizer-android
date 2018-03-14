package org.fossasia.openevent.app.common.data.repository.contract;

import org.fossasia.openevent.app.common.data.models.Faq;

import io.reactivex.Observable;

public interface IFaqRepository {

    Observable<Faq> getFaqs(long id, boolean reload);

    Observable<Faq> createFaq(Faq faq);

}
