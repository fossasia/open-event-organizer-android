package org.fossasia.openevent.app.data.contract;

import io.reactivex.Observable;

public interface IBus<T> {

    void pushItem(T item);

    Observable<T> getItem();

}
