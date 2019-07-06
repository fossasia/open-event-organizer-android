package com.eventyay.organizer.core.ticket.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class TicketsPresenter extends AbstractDetailPresenter<Long, TicketsView> {

    private final List<Ticket> tickets = new ArrayList<>();
    private final TicketRepository ticketRepository;
    private final DatabaseChangeListener<Ticket> ticketChangeListener;

    public boolean isNewTicketCreated = false;

    @Inject
    public TicketsPresenter(TicketRepository ticketRepository, DatabaseChangeListener<Ticket> ticketChangeListener) {
        this.ticketRepository = ticketRepository;
        this.ticketChangeListener = ticketChangeListener;
    }

    @Override
    public void start() {
        loadTickets(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        ticketChangeListener.stopListening();
    }

    private void listenChanges() {
        ticketChangeListener.startListening();
        ticketChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(ticketModelChange -> loadTickets(false), Logger::logError);
    }

    public void loadTickets(boolean forceReload) {
        getTicketSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toSortedList()
            .compose(emptiable(getView(), tickets))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Ticket> getTicketSource(boolean forceReload) {
        if (!forceReload && !tickets.isEmpty() && isRotated() && !isNewTicketCreated)
            return Observable.fromIterable(tickets);
        else
            return ticketRepository.getTickets(getId(), forceReload);
    }

    public void deleteTicket(Ticket ticket) {
        ticketRepository
            .deleteTicket(ticket.getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> {
                getView().showTicketDeleted("Ticket Deleted. Refreshing Items");
                loadTickets(true);
            }, Logger::logError);
    }

    public void showDetails(Ticket ticket) {
        getView().openTicketDetailFragment(ticket.getId());
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
