package com.eventyay.organizer.core.event.chart;

import com.github.mikephil.charting.charts.LineChart;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;

public interface ChartView extends Progressive, Erroneous {

    LineChart getChartView();

}
