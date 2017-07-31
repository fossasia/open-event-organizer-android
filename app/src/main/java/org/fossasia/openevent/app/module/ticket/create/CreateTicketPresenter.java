package org.fossasia.openevent.app.module.ticket.create;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;

public class CreateTicketPresenter extends BasePresenter<ICreateTicketView> implements ICreateTicketPresenter {

    private final ITicketRepository ticketRepository;

    @Inject
    public CreateTicketPresenter(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    @Override
    public void createTicket(Ticket ticket) {
        ticket.setEvent(ContextManager.getSelectedEvent());

        ticketRepository
            .createTicket(ticket)
            .compose(dispose(getDisposable()))
            .compose(erroneous(getView()))
            .doOnSubscribe(disposable -> ticket.creating.set(true))
            .doFinally(() -> ticket.creating.set(false))
            .subscribe(createdTicket -> {
                getView().onSuccess("Ticket Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
