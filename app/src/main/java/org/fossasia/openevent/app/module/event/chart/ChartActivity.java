package org.fossasia.openevent.app.module.event.chart;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.module.event.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.module.event.chart.contract.IChartView;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseActivity;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;

public class ChartActivity extends BaseActivity<IChartPresenter> implements IChartView {
    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.fabExit)
    FloatingActionButton fabExit;

    @BindView(R.id.progressView)
    View progressView;

    @Inject
    Lazy<IChartPresenter> presenterProvider;

    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent()
            .inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chart);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ButterKnife.bind(this);

        eventId = getIntent().getLongExtra(EventDashboardFragment.EVENT_ID, -1);

        fabExit.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hides the status bar
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public Lazy<IChartPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.activity_chart;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(progressView, show);
    }

    @Override
    public LineChart getChartView() {
        return chart;
    }
}
