package org.fossasia.openevent.app.common.data.repository;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.models.Faq_Table;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.contract.IFAQRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import timber.log.Timber;

public class FaqRepository extends Repository implements IFAQRepository {

    @Inject
    public FaqRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @Override
    public Observable<Faq> getFaqs(long eventId, boolean reload) {
        Observable<Faq> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Faq.class, Faq_Table.event_id.eq(eventId))
        );

        Observable<Faq> networkObservable = Observable.defer(() ->
            eventService.getFaqs(eventId)
                .doOnNext(faqs -> {
                    Timber.d(faqs.toString());
                    databaseRepository
                    .deleteAll(Faq.class)
                    .concatWith(databaseRepository.saveList(Faq.class, faqs))
                    .subscribe(); })
                .flatMapIterable(faqs -> faqs));

        return new AbstractObservableBuilder<Faq>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
