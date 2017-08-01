package org.fossasia.openevent.app.module.ticket.create;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketView;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;

public class CreateTicketPresenter extends BasePresenter<ICreateTicketView> implements ICreateTicketPresenter {

    private final ITicketRepository ticketRepository;
    private final Ticket ticket = new Ticket();
    private final IBus bus;
    private Event selected;

    @Inject
    public CreateTicketPresenter(ITicketRepository ticketRepository, IBus bus) {
        this.ticketRepository = ticketRepository;
        this.bus = bus;
    }

    @Override
    public void start() {
        bus.getSelectedEvent()
            .compose(dispose(getDisposable()))
            .subscribeOn(Schedulers.io())
            .subscribe(
                event -> this.selected = event,
                Logger::logError
            );
    }

    @Override
    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public void createTicket() {
        ticket.setEvent(selected);

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
