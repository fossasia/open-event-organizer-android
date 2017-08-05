package org.fossasia.openevent.app.module.ticket.create;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketView;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;

public class CreateTicketPresenter extends BasePresenter<ICreateTicketView> implements ICreateTicketPresenter {

    private final ITicketRepository ticketRepository;
    private final Ticket ticket = new Ticket();

    @Inject
    public CreateTicketPresenter(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        ticket.getSalesStartsAt().set(isoDate);
        ticket.getSalesEndsAt().set(isoDate);
        ticket.setType("free");
    }

    @Override
    public void start() {
        // Nothing to do
    }

    @Override
    public Ticket getTicket() {
        return ticket;
    }

    private boolean verify() {
        ZonedDateTime start = DateUtils.getDate(ticket.getSalesStartsAt().get());
        ZonedDateTime end = DateUtils.getDate(ticket.getSalesEndsAt().get());

        if (!end.isAfter(start)) {
            getView().showError("End time should be after start time");
            return false;
        }

        return true;
    }

    @Override
    public void createTicket() {
        if (!verify())
            return;

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
