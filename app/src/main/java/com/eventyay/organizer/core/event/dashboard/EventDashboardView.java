package com.eventyay.organizer.core.event.dashboard;

import com.github.mikephil.charting.charts.LineChart;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventStatistics;
import com.eventyay.organizer.data.order.OrderStatistics;

public interface EventDashboardView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {

    LineChart getSalesChartView();

    LineChart getCheckinTimeChartView();

    void showChartSales(boolean show);

    void showChartCheckIn(boolean show);

    void showStatistics(EventStatistics eventStatistics);

    void showOrderStatistics(OrderStatistics orderStatistics);

    void showEventUnpublishDialog();

    void switchEventState();

    void showEventLocationDialog();

    void showEventShareDialog();
}
