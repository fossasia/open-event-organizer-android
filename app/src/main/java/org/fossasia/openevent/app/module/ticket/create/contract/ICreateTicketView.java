package org.fossasia.openevent.app.module.ticket.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface ICreateTicketView extends Erroneous, Successful {

    void dismiss();

}
