package com.eventyay.organizer.data.ticket;

import androidx.annotation.NonNull;
import com.eventyay.organizer.utils.CompareUtils;

public class TicketDelegateImpl implements TicketDelegate {

    private final Ticket ticket;

    public TicketDelegateImpl(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public int compareTo(@NonNull Ticket otherOne) {
        return CompareUtils.compareCascading(ticket, otherOne, Ticket::getType);
    }
}
