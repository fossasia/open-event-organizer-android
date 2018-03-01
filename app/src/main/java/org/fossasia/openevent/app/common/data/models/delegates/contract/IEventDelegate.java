package org.fossasia.openevent.app.common.data.models.delegates.contract;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.contract.IHeaderProvider;

import java.util.List;

public interface IEventDelegate extends Comparable<Event>, IHeaderProvider {

    List<Ticket> getEventTickets();

    List<Faq> getEventFaqs();

}
