package org.fossasia.openevent.app.core.orders.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.data.order.OrderRepository;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.data.ticket.TicketRepository;
import org.fossasia.openevent.app.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;

public class OrderCreateViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();

    @Inject
    public OrderCreateViewModel(OrderRepository orderRepository, EventRepository eventRepository,
                                TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        progress.setValue(false);
    }

    public void getEvent(long eventId) {
        compositeDisposable.add(eventRepository.getEvent(eventId, false)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(event -> orderLiveData.getValue().setEvent(event),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<List<Ticket>> getTicketsUnderOrder(long eventId, boolean reload) {
        if (ticketsLiveData.getValue() != null && !reload)
            return ticketsLiveData;

        compositeDisposable.add(ticketRepository.getTickets(eventId, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .toList()
            .subscribe(ticketsLiveData::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        return ticketsLiveData;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
