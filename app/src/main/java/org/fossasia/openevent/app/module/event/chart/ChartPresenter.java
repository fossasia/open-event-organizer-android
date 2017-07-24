package org.fossasia.openevent.app.module.event.chart;

import org.fossasia.openevent.app.module.event.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.module.event.chart.contract.IChartView;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.module.event.dashboard.analyser.ChartAnalyser;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class ChartPresenter extends BaseDetailPresenter<Long, IChartView> implements IChartPresenter {

    private final ChartAnalyser chartAnalyser;

    @Inject
    public ChartPresenter(ChartAnalyser chartAnalyser) {
        this.chartAnalyser = chartAnalyser;
    }

    @Override
    public void start() {
        loadChart();
    }

    @Override
    public void loadChart() {
        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> chartAnalyser.showChart(getView().getChartView()), Logger::logError);
    }
}
