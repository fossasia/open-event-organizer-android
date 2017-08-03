package org.fossasia.openevent.app.module.ticket.detail.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.data.models.Ticket;

public interface ITicketDetailView extends Erroneous, ItemResult<Ticket> {
}
