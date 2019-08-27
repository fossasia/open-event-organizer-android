package com.eventyay.organizer.core.event.list.sales;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.data.event.Event;

public interface SalesSummaryView extends Progressive, Erroneous, ItemResult<Event> {}
