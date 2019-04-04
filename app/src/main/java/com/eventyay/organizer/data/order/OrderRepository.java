package com.eventyay.organizer.data.order;

import androidx.annotation.NonNull;

import com.eventyay.organizer.data.order.model.OrderReceiptRequest;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface OrderRepository {

    Observable<Order> createOrder(Order order);

    Observable<Order> getOrders(long id, boolean reload);

    @NonNull
    Observable<Order> getOrder(String orderIdentifier, boolean reload);

    Observable<OrderStatistics> getOrderStatisticsForEvent(long eventId, boolean reload);

    Completable sendReceipt(OrderReceiptRequest orderReceipt);
}
