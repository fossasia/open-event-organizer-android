package org.fossasia.openevent.app.module.ticket.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Ticket;

public interface ICreateTicketPresenter extends IPresenter<ICreateTicketView> {

    Ticket getTicket();

    void createTicket();

}
