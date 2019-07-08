package com.eventyay.organizer.core.ticket.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.core.ticket.list.TicketsPresenter;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.ErrorUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateTicketViewModel extends ViewModel {

    private final TicketRepository ticketRepository;
    private final Ticket ticket = new Ticket();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();

    @Inject
    public CreateTicketViewModel(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
        LocalDateTime current = LocalDateTime.now();
        String startDate = DateUtils.formatDateToIso(current);
        ticket.setSalesStartsAt(startDate);

        LocalDateTime salesEndTime = DateUtils.getIsoOffsetTimeFromTimestamp(
            ContextManager.getSelectedEvent().getStartsAt());

        String endDate = DateUtils.formatDateToIso(salesEndTime);
        ticket.setSalesEndsAt(endDate);
        ticket.setType("free");
    }

    public Ticket getTicket() {
        return ticket;
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(ticket.getSalesStartsAt());
            ZonedDateTime end = DateUtils.getDate(ticket.getSalesEndsAt());

            if (!end.isAfter(start)) {
                error.setValue("End time should be after start time");
                return false;
            }
            if (ticket.minOrder != null && ticket.maxOrder != null && ticket.minOrder > ticket.maxOrder) {
                error.setValue("Minimum order should be greater than Maximum order");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            error.setValue("Please enter date in correct format");
            return false;
        }
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createTicket() {
        if (!verify())
            return;

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        ticket.setEvent(event);

        compositeDisposable.add(
            ticketRepository
                .createTicket(ticket)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(createdTicket -> {
                    success.setValue("Ticket Created");
                    TicketsPresenter.isNewTicketCreated = true;
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
