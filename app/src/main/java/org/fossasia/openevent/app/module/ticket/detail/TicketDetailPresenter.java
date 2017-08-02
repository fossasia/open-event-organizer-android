package org.fossasia.openevent.app.module.ticket.detail;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.module.ticket.detail.contract.ITicketDetailPresenter;
import org.fossasia.openevent.app.module.ticket.detail.contract.ITicketDetailView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousResult;

public class TicketDetailPresenter extends BaseDetailPresenter<Long, ITicketDetailView> implements ITicketDetailPresenter {

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
