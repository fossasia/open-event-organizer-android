package org.fossasia.openevent.app.event.detail.contract;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.ItemResult;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Refreshable;
import org.fossasia.openevent.app.data.models.Event;

public interface IEventDetailView extends Progressive, Erroneous, Refreshable, ItemResult<Event> {
    LineChart getChartView();
}
