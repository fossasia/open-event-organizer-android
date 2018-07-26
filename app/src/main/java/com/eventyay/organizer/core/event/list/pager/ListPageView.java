package com.eventyay.organizer.core.event.list.pager;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.data.event.Event;

public interface ListPageView extends Emptiable<Event> {

    void openSalesSummary(Long id);

    void closeSalesSummary();

}
