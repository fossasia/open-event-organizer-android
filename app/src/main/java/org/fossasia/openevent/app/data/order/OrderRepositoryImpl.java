package org.fossasia.openevent.app.data.order;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.RateLimiter;
import org.fossasia.openevent.app.data.Repository;
import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Observable;

public class OrderRepositoryImpl implements OrderRepository {

    private final OrderApi orderApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public OrderRepositoryImpl(OrderApi orderApi, Repository repository) {
        this.orderApi = orderApi;
        this.repository = repository;
    }

    @Override
    public Observable<Order> getOrders(long eventId, boolean reload) {
        Observable<Order> diskObservable = Observable.defer(() ->
            repository.getItems(Order.class, Order_Table.event_id.eq(eventId))
        );

        Observable<Order> networkObservable = Observable.defer(() ->
            orderApi.getOrders(eventId)
                .doOnNext(orders -> repository.syncSave(Order.class, orders, Order::getId, Order_Table.id).subscribe())
                .flatMapIterable(orders -> orders));

        return repository.observableOf(Order.class)
            .reload(reload)
            .withRateLimiterConfig("Orders", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Order> getOrder(String orderIdentifier, boolean reload) {
        Observable<Order> diskObservable = Observable.defer(() ->
            repository
                .getItems(Order.class, Order_Table.identifier.eq(orderIdentifier)).take(1)
        );

        Observable<Order> networkObservable = Observable.defer(() ->
            orderApi.getOrder(orderIdentifier)
                .doOnNext(order -> repository
                    .save(Order.class, order)
                    .subscribe()));

        return repository
            .observableOf(Order.class)
            .reload(reload)
            .withRateLimiterConfig("Order", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
