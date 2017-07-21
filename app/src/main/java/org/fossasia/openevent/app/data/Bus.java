package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.IBus;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class Bus<T> implements IBus<T> {
    private final PublishSubject<T> itemPublisher = PublishSubject.create();

    @Inject
    public Bus() {}

    @Override
    public void pushItem(T item) {
        itemPublisher.onNext(item);
    }

    @Override
    public Observable<T> getItem() {
        return itemPublisher;
    }

}
