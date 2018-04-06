package org.fossasia.openevent.app.core.event.chart;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;

public interface ChartView extends Progressive, Erroneous {

    LineChart getChartView();

}
