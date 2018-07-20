package org.fossasia.openevent.app.core.event.dashboard;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventStatistics;

public interface EventDashboardView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {

    LineChart getSalesChartView();

    LineChart getCheckinTimeChartView();

    void showChartSales(boolean show);

    void showChartCheckIn(boolean show);

    void showStatistics(EventStatistics eventStatistics);

    void showEventUnpublishDialog();

    void showShareDialog();

    void switchEventState();
}
