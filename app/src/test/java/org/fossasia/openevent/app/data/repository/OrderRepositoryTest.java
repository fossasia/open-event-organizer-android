package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.AbstractObservable;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.data.order.OrderApi;
import org.fossasia.openevent.app.data.order.OrderRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private OrderRepositoryImpl orderRepository;
    private static final String ORDER_IDENTIFIER = "617ed24c-9a07-4084-b076-ed73552db27e";
    private static final Order ORDER = Order.builder().id(10L).identifier(ORDER_IDENTIFIER).build();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private OrderApi orderApi;
    @Mock private Repository repository;

    static {
        ORDER.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Order.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        orderRepository = new OrderRepositoryImpl(orderApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    //Network Down Tests

    @Test
    public void shouldReturnConnectionErrorOnGetOrdersWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Order> orderObservable = orderRepository.getOrders(ID, true);

        orderObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetOrdersWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Order.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Order> orderObservable = orderRepository.getOrders(ID, false);

        orderObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Orders Get Tests

    @Test
    public void shouldCallGetOrdersServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrders(ID)).thenReturn(Observable.empty());

        orderRepository.getOrders(ID, true).subscribe();

        verify(orderApi).getOrders(ID);
    }

    @Test
    public void shouldCallGetOrdersServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrders(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Order.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        orderRepository.getOrders(ID, false).subscribe();

        verify(orderApi).getOrders(ID);
    }

    @Test
    public void shouldSaveOrdersOnGet() {
        List<Order> orders = new ArrayList<>();
        orders.add(ORDER);

        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrders(ID)).thenReturn(Observable.just(orders));
        when(repository.syncSave(eq(Order.class), eq(orders), any(), any())).thenReturn(Completable.complete());

        orderRepository.getOrders(ID, true).subscribe();

        verify(repository).syncSave(eq(Order.class), eq(orders), any(), any());
    }

    // Order Get Tests

    @Test
    public void shouldCallGetOrderServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrder(ORDER_IDENTIFIER)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Order.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        orderRepository.getOrder(ORDER_IDENTIFIER, true).subscribe();

        verify(orderApi).getOrder(ORDER_IDENTIFIER);
    }

    @Test
    public void shouldCallGetOrderServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrder(ORDER_IDENTIFIER)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Order.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        orderRepository.getOrder(ORDER_IDENTIFIER, false).subscribe();

        verify(orderApi).getOrder(ORDER_IDENTIFIER);
    }

    @Test
    public void shouldSaveOrderOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(orderApi.getOrder(ORDER_IDENTIFIER)).thenReturn(Observable.just(ORDER));
        when(repository.save(eq(Order.class), eq(ORDER))).thenReturn(Completable.complete());
        when(repository.getItems(eq(Order.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        orderRepository.getOrder(ORDER_IDENTIFIER, true).subscribe();

        verify(repository).save(Order.class, ORDER);
    }
}
