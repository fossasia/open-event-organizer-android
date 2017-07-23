package org.fossasia.openevent.app.chart;

import org.fossasia.openevent.app.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.chart.contract.IChartView;
import org.fossasia.openevent.app.common.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.event.detail.ChartAnalyser;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

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
