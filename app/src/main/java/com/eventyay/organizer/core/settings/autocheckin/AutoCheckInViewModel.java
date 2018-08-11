package com.eventyay.organizer.core.settings.autocheckin;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class AutoCheckInViewModel extends ViewModel {

    private final MutableLiveData<List<Ticket>> tickets = new MutableLiveData<>();
    private final TicketRepository ticketRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> ticketUpdatedAction = new SingleEventLiveData<>();

    @Inject
    public AutoCheckInViewModel(TicketRepository ticketRepository) {
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

    public void updateAllTickets(boolean toAutoCheckIn) {
        compositeDisposable.add(
            Observable.fromIterable(tickets.getValue())
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(ticket -> {
                    ticket.autoCheckinEnabled = toAutoCheckIn;
                    updateTicket(ticket);
                }, Logger::logError)
        );
    }

    public LiveData<List<Ticket>> getTickets() {
        return tickets;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Void> getTicketUpdatedAction() {
        return ticketUpdatedAction;
    }

}
