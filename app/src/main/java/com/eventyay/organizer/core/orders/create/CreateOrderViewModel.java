package com.eventyay.organizer.core.orders.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableLong;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.ticket.OnSiteTicket;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.ErrorUtils;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;

public class CreateOrderViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleEventLiveData<Float> orderAmount = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventMutableLiveData = new MutableLiveData<>();
    private final List<OnSiteTicket> onSiteTicketsList = new ArrayList<>();
    private final Map<Long, ObservableLong> onSiteTicketsMap =  new ConcurrentHashMap<>();

    private Event event = new Event();

    @Inject
    public CreateOrderViewModel(OrderRepository orderRepository, TicketRepository ticketRepository, EventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        progress.setValue(false);
        orderAmount.setValue(0F);
    }

    public LiveData<List<Ticket>> getTicketsUnderEvent(long eventId, boolean reload) {
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

    public void ticketClick(Ticket ticket) {
        Long ticketId = ticket.getId();

        if (onSiteTicketsMap.get(ticketId).get() == ticket.getMaxOrder()) {
            error.setValue("Cannot exceed maximum order limit");
            return;
        }

        if (onSiteTicketsMap.containsKey(ticketId)) {
            Long quantity = onSiteTicketsMap.get(ticketId).get();
            onSiteTicketsMap.get(ticketId).set(quantity + 1L);

        } else {
            onSiteTicketsMap.put(ticketId, new ObservableLong(1L));
        }

        if (orderAmount.getValue() != null) {
            float amount = orderAmount.getValue();
            orderAmount.setValue(amount + ticket.getPrice());
        } else {
            orderAmount.setValue(0F);
        }
    }

    public LiveData<Event> getEventLiveData() {
        eventMutableLiveData.setValue(event);
        return eventMutableLiveData;
    }

    public void loadEvent(long eventId) {
        compositeDisposable.add(eventRepository
            .getEvent(eventId, false)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> {
                progress.setValue(false);
                getEventLiveData();
            })
            .subscribe(loadedEvent -> this.event = loadedEvent, Logger::logError));
    }

    public ObservableLong getOnSiteTicketQuantity(Long ticketId) {
        if (onSiteTicketsMap.containsKey(ticketId)) {
            return onSiteTicketsMap.get(ticketId);
        } else {
            onSiteTicketsMap.put(ticketId, new ObservableLong(0L));
            return onSiteTicketsMap.get(ticketId);
        }
    }

    public boolean verify() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime endDate = DateUtils.getDate(event.getEndsAt());

        if (endDate.isBefore(now)) {
            error.setValue("Event has ended");
            return false;
        }
        return true;
    }

    public void createOnSiteOrder() {
        if (!verify())
            return;

        Order order = new Order();
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
                clearSelectedTickets();
                progress.setValue(false);
            })
            .subscribe(createdOrder -> success.setValue("Order created successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void clearSelectedTickets() {
        for (Long ticketId : onSiteTicketsMap.keySet()) {
            onSiteTicketsMap.get(ticketId).set(0L);
        }
        orderAmount.setValue(0F);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
