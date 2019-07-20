package com.eventyay.organizer.core.orders.onsite;

import androidx.databinding.ObservableLong;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.ticket.OnSiteTicket;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;

public class CreateAttendeesViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleEventLiveData<Float> orderAmount = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();
    private final List<OnSiteTicket> onSiteTicketsList = new ArrayList<>();
    private final Map<Long, ObservableLong> onSiteTicketsMap =  new ConcurrentHashMap<>();
    private final MutableLiveData<List<Attendee>> attendeesLiveData = new MutableLiveData<>();

    private List<Attendee> attendeeList;

    @Inject
    public CreateAttendeesViewModel(OrderRepository orderRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
    }

    public void createOnSiteOrder(long eventId) {
        Order order = new Order();
        Event event = new Event();
        event.setId(eventId);
        order.setEvent(event);
        order.setAmount(orderAmount.getValue());

        for (Long ticketId : onSiteTicketsMap.keySet()) {
            OnSiteTicket onSiteTicket = new OnSiteTicket();
            onSiteTicket.setId(ticketId);
            onSiteTicket.setQuantity(onSiteTicketsMap.get(ticketId).get());
            onSiteTicketsList.add(onSiteTicket);
        }

        order.setOnSiteTickets(onSiteTicketsList);

        compositeDisposable.add(orderRepository.createOrder(order)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> {
                progress.setValue(false);
            })
            .subscribe(createdOrder -> success.setValue("Order created successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void setAttendeeList(Attendee attendee) {
        attendeeList.add(attendee);
    }

    public LiveData<Float> getOrderAmount() {
        return orderAmount;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<List<Attendee>> getAttendeesLiveData() {
        return attendeesLiveData;
    }
}
