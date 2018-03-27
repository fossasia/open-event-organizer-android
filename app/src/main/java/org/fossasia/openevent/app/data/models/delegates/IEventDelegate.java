package org.fossasia.openevent.app.data.models.delegates;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.models.Ticket;

import java.util.List;

public interface IEventDelegate extends Comparable<Event>, IHeaderProvider {

    List<Ticket> getEventTickets();

    List<Faq> getEventFaqs();

}
