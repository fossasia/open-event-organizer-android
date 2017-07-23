package org.fossasia.openevent.app.event.tickets.contract;

import org.fossasia.openevent.app.common.contract.view.Emptiable;
import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Refreshable;
import org.fossasia.openevent.app.data.models.Ticket;

public interface ITicketsView extends Progressive, Erroneous, Refreshable, Emptiable<Ticket> {
}
