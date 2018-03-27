package org.fossasia.openevent.app.core.ticket.create;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.repository.ITicketRepository;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateTicketPresenter extends BasePresenter<ICreateTicketView> {

    private final ITicketRepository ticketRepository;
    private final Ticket ticket = new Ticket();

    @Inject
    public CreateTicketPresenter(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
        LocalDateTime current = LocalDateTime.now();
        String startDate = DateUtils.formatDateToIso(current);
        ticket.getSalesStartsAt().set(startDate);

        LocalDateTime salesEndTime = current.plusDays(10);
        LocalDateTime eventEndTime = DateUtils.getIsoOffsetTimeFromTimestamp(ContextManager.getSelectedEvent().getEndsAt().get());
        //if less than 10 days are available in the event.
        if (salesEndTime.isAfter(eventEndTime)) {
            salesEndTime = eventEndTime;
        }
        String endDate = DateUtils.formatDateToIso(salesEndTime);
        ticket.getSalesEndsAt().set(endDate);
        ticket.setType("free");
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Ticket getTicket() {
        return ticket;
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(ticket.getSalesStartsAt().get());
            ZonedDateTime end = DateUtils.getDate(ticket.getSalesEndsAt().get());

            if (!end.isAfter(start)) {
                getView().showError("End time should be after start time");
                return false;
            }
            if (ticket.minOrder != null && ticket.maxOrder != null && ticket.minOrder > ticket.maxOrder) {
                getView().showError("Minimum order should be greater than Maximum order");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            getView().showError("Please enter date in correct format");
            return false;
        }
    }

    public void createTicket() {
        if (!verify())
            return;

        ticket.setEvent(ContextManager.getSelectedEvent());

        ticketRepository
            .createTicket(ticket)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdTicket -> {
                getView().onSuccess("Ticket Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
