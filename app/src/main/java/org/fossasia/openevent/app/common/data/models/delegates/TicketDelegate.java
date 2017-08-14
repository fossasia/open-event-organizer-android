package org.fossasia.openevent.app.common.data.models.delegates;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.delegates.contract.ITicketDelegate;
import org.fossasia.openevent.app.common.utils.core.CompareUtils;

public class TicketDelegate implements ITicketDelegate {

    private final Ticket ticket;

    public TicketDelegate(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public int compareTo(@NonNull Ticket otherOne) {
        return CompareUtils.compareCascading(ticket, otherOne, Ticket::getType);
    }

}
