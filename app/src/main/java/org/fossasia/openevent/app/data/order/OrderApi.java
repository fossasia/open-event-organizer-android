package org.fossasia.openevent.app.data.order;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderApi {

    @GET("events/{id}/orders?include=event&fields[event]=id&page[size]=0")
    Observable<List<Order>> getOrders(@Path("id") long id);

    @GET("orders/{identifier}?include=event")
    Observable<Order> getOrder(@Path("identifier") String identifier);

    @GET("events/{event_id}/order-statistics")
    Observable<OrderStatistics> getOrderStatisticsForEvent(@Path("event_id") long id);
}
