package org.fossasia.openevent.app.core.event.dashboard;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.EventStatistics;

public interface IEventDashboardView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {

    LineChart getChartView();

    void showChart(boolean show);

    void showStatistics(EventStatistics eventStatistics);
}
