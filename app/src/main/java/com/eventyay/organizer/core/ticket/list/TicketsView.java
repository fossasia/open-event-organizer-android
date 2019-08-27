package com.eventyay.organizer.core.ticket.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.ticket.Ticket;

public interface TicketsView extends Progressive, Erroneous, Refreshable, Emptiable<Ticket> {

    void showTicketDeleted(String message);

    void openTicketDetailFragment(long ticketId);
}
