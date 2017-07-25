package org.fossasia.openevent.app.module.event.chart.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;

public interface IChartPresenter extends IDetailPresenter<Long, IChartView> {

    void loadChart();

}
