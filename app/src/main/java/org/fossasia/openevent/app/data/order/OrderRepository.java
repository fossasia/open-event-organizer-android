package org.fossasia.openevent.app.data.order;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface OrderRepository {

    Observable<Order> getOrders(long id, boolean reload);

    @NonNull
    Observable<Order> getOrder(String orderIdentifier, boolean reload);

    Observable<OrderStatistics> getOrderStatisticsForEvent(long eventId, boolean reload);
}
