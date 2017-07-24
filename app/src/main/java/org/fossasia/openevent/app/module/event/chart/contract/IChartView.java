package org.fossasia.openevent.app.module.event.chart.contract;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;

public interface IChartView extends Progressive, Erroneous{

    LineChart getChartView();

}
