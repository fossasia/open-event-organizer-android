package org.fossasia.openevent.app.chart.contract;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.Progressive;

public interface IChartView extends Progressive, Erroneous{

    LineChart getChartView();

}
