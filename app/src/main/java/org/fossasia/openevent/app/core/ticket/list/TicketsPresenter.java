package org.fossasia.openevent.app.core.ticket.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.data.ticket.TicketRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class TicketsPresenter extends AbstractDetailPresenter<Long, TicketsView> {

    private final List<Ticket> tickets = new ArrayList<>();
    private final TicketRepository ticketRepository;
    private final DatabaseChangeListener<Ticket> ticketChangeListener;

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
        if (!forceReload && !tickets.isEmpty() && isRotated())
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
