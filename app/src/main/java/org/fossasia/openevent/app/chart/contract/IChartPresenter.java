package org.fossasia.openevent.app.chart.contract;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;

public interface IChartPresenter extends IDetailPresenter<Long, IChartView> {

    void loadChart();

}
