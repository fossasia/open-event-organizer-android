package org.fossasia.openevent.app.core.ticket.detail;

import org.fossasia.openevent.app.common.mvp.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.repository.ITicketRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;

public class TicketDetailPresenter extends BaseDetailPresenter<Long, ITicketDetailView> {

    private final ITicketRepository ticketRepository;

    @Inject
    public TicketDetailPresenter(ITicketRepository ticketRepository) {
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
