package com.eventyay.organizer.core.settings.restriction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class TicketSettingsViewModel extends ViewModel {

    private final MutableLiveData<List<Ticket>> tickets = new MutableLiveData<>();
    private final TicketRepository ticketRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final SingleEventLiveData<Void> ticketUpdatedAction = new SingleEventLiveData<>();

    @Inject
    public TicketSettingsViewModel(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void loadTickets(long eventId) {
        compositeDisposable.add(ticketRepository.getTickets(eventId, true)
            .toSortedList()
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(tickets::setValue,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void updateTicket(Ticket ticket) {
        compositeDisposable.add(
            ticketRepository
                .updateTicket(ticket)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedTicket -> {
                    ticketUpdatedAction.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void updateAllTickets(boolean toRestrict) {
        compositeDisposable.add(
            Observable.fromIterable(tickets.getValue())
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(ticket -> {
                    ticket.isCheckinRestricted = toRestrict;
                    updateTicket(ticket);
                }, Logger::logError)
        );
    }

    public MutableLiveData<List<Ticket>> getTickets() {
        return tickets;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public SingleEventLiveData<Void> getTicketUpdatedAction() {
        return ticketUpdatedAction;
    }
}
