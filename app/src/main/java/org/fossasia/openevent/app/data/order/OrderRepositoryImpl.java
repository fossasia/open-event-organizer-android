package org.fossasia.openevent.app.data.order;

import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class OrderRepositoryImpl implements OrderRepository {

    private final OrderApi orderApi;
    private final Repository repository;

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
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
