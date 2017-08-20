package org.fossasia.openevent.app.module.event.dashboard.contract;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Refreshable;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;
import org.fossasia.openevent.app.common.data.models.Event;

public interface IEventDashboardView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {
    LineChart getChartView();

    void showChart(boolean show);
}
