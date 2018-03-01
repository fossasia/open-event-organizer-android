package org.fossasia.openevent.app.common.data.repository.contract;

import org.fossasia.openevent.app.common.data.models.Faq;

import io.reactivex.Observable;

public interface IFAQRepository {

    Observable<Faq> getFaqs(long id, boolean reload);

}
