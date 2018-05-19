package org.fossasia.openevent.app.core.event.chart;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseInjectActivity;
import org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;

public class ChartActivity extends BaseInjectActivity<ChartPresenter> implements ChartView {
    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.fabExit)
    com.google.android.material.floatingactionbutton.FloatingActionButton fabExit;

    @BindView(R.id.progressBar)
    View progressView;

    @Inject
    Lazy<ChartPresenter> presenterProvider;

    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public Lazy<ChartPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(chart, error);
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
