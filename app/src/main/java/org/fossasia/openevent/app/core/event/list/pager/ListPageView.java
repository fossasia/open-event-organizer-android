package org.fossasia.openevent.app.core.event.list.pager;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.data.event.Event;

public interface ListPageView extends Emptiable<Event> {

    void openSalesSummary(Long id);

    void closeSalesSummary();

}
