package com.eventyay.organizer.core.ticket.detail;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.erroneousResult;

public class TicketDetailPresenter extends AbstractDetailPresenter<Long, TicketDetailView> {

    private final TicketRepository ticketRepository;
    private Ticket ticket = new Ticket();

    @Inject
    public TicketDetailPresenter(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public void start() {
        if (getView() == null)
            return;

        loadTicket();
    }

    private void loadTicket() {
        ticketRepository
            .getTicket(getId(), false)
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(loadedTicket -> {
               ticket = loadedTicket;
            }, Logger::logError);
    }
}
