package com.eventyay.organizer.core.orders.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.core.presenter.TestUtil;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrdersViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private OrderRepository orderRepository;
    @Mock
    Observer<List<Order>> orders;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;

    private OrdersViewModel ordersViewModel;

    private static final long EVENT_ID = 5L;
    private static final List<Order> ORDERS_LIST = Arrays.asList(
        Order.builder().id(12L).build(),
        Order.builder().id(13L).build(),
        Order.builder().id(14L).build()
    );

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        ordersViewModel = new OrdersViewModel(orderRepository);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadOrdersSuccessfully() {
        when(orderRepository.getOrders(EVENT_ID, false))
            .thenReturn(Observable.fromIterable(ORDERS_LIST));

        InOrder inOrder = Mockito.inOrder(orders, orderRepository, progress);

        ordersViewModel.getProgress().observeForever(progress);

        orders.onChanged(new ArrayList<>());

        ordersViewModel.getOrders(EVENT_ID, false);

        inOrder.verify(orders).onChanged(new ArrayList<>());
        inOrder.verify(orderRepository).getOrders(EVENT_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRefreshOrdersSuccessfully() {
        when(orderRepository.getOrders(EVENT_ID, true))
            .thenReturn(Observable.fromIterable(ORDERS_LIST));

        InOrder inOrder = Mockito.inOrder(orders, orderRepository, progress);

        ordersViewModel.getProgress().observeForever(progress);
        ordersViewModel.getError().observeForever(error);

        orders.onChanged(new ArrayList<>());

        ordersViewModel.getOrders(EVENT_ID, true);

        inOrder.verify(orderRepository).getOrders(EVENT_ID, true);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowOrderError() {
        String errorString = "Test Error";
        when(orderRepository.getOrders(EVENT_ID, false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(orderRepository, progress, error);

        ordersViewModel.getProgress().observeForever(progress);
        ordersViewModel.getError().observeForever(error);

        orders.onChanged(new ArrayList<>());

        ordersViewModel.getOrders(EVENT_ID, false);

        inOrder.verify(orderRepository).getOrders(EVENT_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }
}
