package org.fossasia.openevent.app.core.ticket.list;


import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.ticket.Ticket;

public interface TicketsView extends Progressive, Erroneous, Refreshable, Emptiable<Ticket> {

    void showTicketDeleted(String message);

    void openTicketDetailFragment(long ticketId);

}
