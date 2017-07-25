package org.fossasia.openevent.app.module.tickets;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.module.tickets.contract.ITicketsPresenter;
import org.fossasia.openevent.app.module.tickets.contract.ITicketsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;


public class TicketsPresenter extends BaseDetailPresenter<Long, ITicketsView> implements ITicketsPresenter {

    private final List<Ticket> tickets = new ArrayList<>();
    private final ITicketRepository ticketRepository;

    @Inject
    public TicketsPresenter(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void start() {
        loadTickets(false);
    }

    @Override
    public void loadTickets(boolean refresh) {
        ticketRepository
            .getTickets(getId(), refresh)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), refresh))
            .toList()
            .compose(emptiable(getView(), tickets))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    @Override
    public List<Ticket> getTickets() {
        return tickets;
    }
}
