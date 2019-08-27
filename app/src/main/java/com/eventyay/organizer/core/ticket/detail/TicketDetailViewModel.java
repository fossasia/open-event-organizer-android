package com.eventyay.organizer.core.ticket.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class TicketDetailViewModel extends ViewModel {

    private final TicketRepository ticketRepository;
    private final MutableLiveData<Ticket> ticket = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();

    @Inject
    public TicketDetailViewModel(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public LiveData<Ticket> getTicket() {
        return ticket;
    }

    public void loadTicket(long ticketId) {
        compositeDisposable.add(
                ticketRepository
                        .getTicket(ticketId, false)
                        .subscribe(
                                ticket::setValue,
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
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
