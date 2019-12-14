package com.eventyay.organizer.core.orders.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;

public class OrdersViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

    @Inject
    public OrdersViewModel(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        progress.setValue(false);
    }

    public LiveData<List<Order>> getOrders(long id, boolean reload) {
        if (ordersLiveData.getValue() != null && !reload)
            return ordersLiveData;

        compositeDisposable.add(orderRepository.getOrders(id, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .toList()
            .subscribe(ordersLiveData::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        return ordersLiveData;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void click(Order order) {
        orderLiveData.setValue(order);
    }

    public void unselectListItem() {
        orderLiveData.setValue(null);
    }

    protected LiveData<Order> getClickedOrder() {
        return orderLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
        orderLiveData.setValue(null);
    }
}
