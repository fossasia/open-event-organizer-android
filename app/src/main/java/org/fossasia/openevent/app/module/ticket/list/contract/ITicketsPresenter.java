package org.fossasia.openevent.app.module.ticket.list.contract;


import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.common.data.models.Ticket;

import java.util.List;

public interface ITicketsPresenter extends IDetailPresenter<Long, ITicketsView> {

    void loadTickets(boolean refresh);

    List<Ticket> getTickets();

}
