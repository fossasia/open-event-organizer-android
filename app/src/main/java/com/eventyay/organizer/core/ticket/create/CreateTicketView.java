package com.eventyay.organizer.core.ticket.create;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface CreateTicketView extends Progressive, Erroneous, Successful {

    void dismiss();
}
