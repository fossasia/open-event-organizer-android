package com.eventyay.organizer.core.event.chart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.event.dashboard.analyser.ChartAnalyser;
import com.github.mikephil.charting.charts.LineChart;

import javax.inject.Inject;

public class ChartViewModel extends ViewModel {

    private final ChartAnalyser chartAnalyser;
    private LineChart chart;
    private long eventId;

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();

    @Inject
    public ChartViewModel(ChartAnalyser chartAnalyser) {
        this.chartAnalyser = chartAnalyser;

        eventId = ContextManager.getSelectedEvent().getId();
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadChart() {
        chartAnalyser.loadData(eventId)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> chartAnalyser.showChart(getChartView()), Logger::logError);
    }

    public void setChartView(LineChart chart) {
        this.chart = chart;
    }

    public LineChart getChartView() {
        return chart;
    }
}
