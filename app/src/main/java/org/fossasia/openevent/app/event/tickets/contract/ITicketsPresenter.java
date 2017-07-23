package org.fossasia.openevent.app.event.tickets.contract;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.data.models.Ticket;

import java.util.List;

public interface ITicketsPresenter extends IDetailPresenter<Long, ITicketsView> {

    void loadTickets(boolean refresh);

    List<Ticket> getTickets();

}
