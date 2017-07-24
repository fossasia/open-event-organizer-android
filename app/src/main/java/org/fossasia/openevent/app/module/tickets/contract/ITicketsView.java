package org.fossasia.openevent.app.module.tickets.contract;


import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Emptiable;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Refreshable;
import org.fossasia.openevent.app.common.data.models.Ticket;

public interface ITicketsView extends Progressive, Erroneous, Refreshable, Emptiable<Ticket> {
}
