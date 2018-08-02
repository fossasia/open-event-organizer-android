package com.eventyay.organizer.core.orders.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.order.model.OrderReceiptRequest;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;

public class OrderDetailViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final MutableLiveData<List<Attendee>> attendeesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();

    @Inject
    public OrderDetailViewModel(OrderRepository orderRepository, EventRepository eventRepository,
                                AttendeeRepository attendeeRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.ticketRepository = ticketRepository;
        progress.setValue(false);
    }

    public LiveData<Order> getOrder(String identifier, long eventId, boolean reload) {
        if (orderLiveData.getValue() != null && !reload)
            return orderLiveData;

        compositeDisposable.add(orderRepository.getOrder(identifier, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(orderLiveData::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        if (!reload) {
            getEvent(eventId);
        }

        return orderLiveData;
    }

    public void getEvent(long eventId) {
        compositeDisposable.add(eventRepository.getEvent(eventId, false)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(event -> orderLiveData.getValue().setEvent(event),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<List<Attendee>> getAttendeesUnderOrder(String orderIdentifier, long orderId, boolean reload) {
        if (attendeesLiveData.getValue() != null && !reload)
            return attendeesLiveData;

        compositeDisposable.add(attendeeRepository.getAttendeesUnderOrder(orderIdentifier, orderId, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .toList()
            .subscribe(attendeesLiveData::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        return attendeesLiveData;
    }

    public LiveData<List<Ticket>> getTicketsUnderOrder(String orderIdentifier, long orderId, boolean reload) {
        if (ticketsLiveData.getValue() != null && !reload)
            return ticketsLiveData;

        compositeDisposable.add(ticketRepository.getTicketsUnderOrder(orderIdentifier, orderId, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .toList()
            .subscribe(ticketsLiveData::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        return ticketsLiveData;
    }

    public void toggleCheckIn(int index) {
        attendeesLiveData.getValue().get(index).setChecking(true);
        attendeesLiveData.getValue().get(index).isCheckedIn = !attendeesLiveData.getValue().get(index).isCheckedIn;

        compositeDisposable.add(attendeeRepository.scheduleToggle(attendeesLiveData.getValue().get(index))
            .subscribe(() -> {
                    //Nothing here
                 },
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public Boolean getCheckedInStatus(int index) {
        return attendeesLiveData.getValue().get(index).isCheckedIn;
    }

    @VisibleForTesting
    public void setAttendees(List<Attendee> attendees) {
        attendeesLiveData.setValue(attendees);
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
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    //send order receipt via email
    public void sendReceipt(String orderIdentifier) {
        OrderReceiptRequest orderReceipt = new OrderReceiptRequest();
        orderReceipt.setOrderIdentifier(orderIdentifier);
        compositeDisposable.add(orderRepository.sendReceipt(orderReceipt)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> success.setValue("Email Sent!"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

}
