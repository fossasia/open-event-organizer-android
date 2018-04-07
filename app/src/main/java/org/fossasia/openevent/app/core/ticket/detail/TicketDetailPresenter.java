package org.fossasia.openevent.app.core.ticket.detail;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.ticket.TicketRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;

public class TicketDetailPresenter extends AbstractDetailPresenter<Long, TicketDetailView> {

    private final TicketRepository ticketRepository;

    @Inject
    public TicketDetailPresenter(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
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
            .subscribe(Logger::logSuccess, Logger::logError);
    }
}
