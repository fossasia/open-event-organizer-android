package org.fossasia.openevent.app.module.ticket.create;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketView;

import java.util.Date;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;

public class CreateTicketPresenter extends BasePresenter<ICreateTicketView> implements ICreateTicketPresenter {

    private final ITicketRepository ticketRepository;
    private final Ticket ticket = new Ticket();

    @Inject
    public CreateTicketPresenter(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
        Date current = new Date();
        ticket.setSalesStartsAt(DateUtils.formatDateToIso(current));
        ticket.setSalesEndsAt(DateUtils.formatDateToIso(current));
    }

    @Override
    public void start() {
        // Nothing to do
    }

    @Override
    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public void createTicket() {
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
