package com.eventyay.organizer.core.event.chart;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.event.dashboard.analyser.ChartAnalyser;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class ChartPresenter extends AbstractDetailPresenter<Long, ChartView> {

    private final ChartAnalyser chartAnalyser;

    @Inject
    public ChartPresenter(ChartAnalyser chartAnalyser) {
        this.chartAnalyser = chartAnalyser;
    }

    @Override
    public void start() {
        loadChart();
    }

    public void loadChart() {
        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> chartAnalyser.showChart(getView().getChartView()), Logger::logError);
    }
}
