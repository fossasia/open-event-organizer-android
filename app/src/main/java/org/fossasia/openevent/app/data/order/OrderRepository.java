package org.fossasia.openevent.app.data.order;

import io.reactivex.Observable;

public interface OrderRepository {

    Observable<Order> getOrders(long id, boolean reload);
}
