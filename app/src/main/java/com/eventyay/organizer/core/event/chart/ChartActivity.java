package com.eventyay.organizer.core.event.chart;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.event.dashboard.EventDashboardFragment;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class ChartActivity extends AppCompatActivity implements ChartView, HasActivityInjector {

    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.fabExit)
    FloatingActionButton fabExit;

    @BindView(R.id.progressBar)
    View progressView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    private ChartViewModel chartViewModel;

    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        chartViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChartViewModel.class);

        setContentView(R.layout.activity_chart);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ButterKnife.bind(this);

        eventId = getIntent().getLongExtra(EventDashboardFragment.EVENT_ID, -1);

        fabExit.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        chartViewModel.getProgress().observe(this, this::showProgress);
        chartViewModel.getError().observe(this, this::showError);
        chartViewModel.setChartView(chart);
        chartViewModel.loadChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hides the status bar
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(chart, error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(progressView, show);
    }
}
