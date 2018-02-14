package org.fossasia.openevent.app.module.event.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.EventDetailBinding;
import org.fossasia.openevent.app.module.event.chart.ChartActivity;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardPresenter;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardView;


import javax.inject.Inject;

import dagger.Lazy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDashboardFragment extends BaseFragment<IEventDashboardPresenter> implements IEventDashboardView {

    public static final String EVENT_ID = "event_id";

    private long initialEventId;
    private EventDetailBinding binding;

    @Inject
    Context context;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<IEventDashboardPresenter> presenterProvider;

    private ConstraintLayout container;
    private SwipeRefreshLayout refreshLayout;
    private EventDashboardViewModel eventDashboardViewModel;

    public EventDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId Event for which the Fragment is to be created.
     * @return A new instance of fragment EventDashboardFragment.
     */
    public static EventDashboardFragment newInstance(long eventId) {
        EventDashboardFragment fragment = new EventDashboardFragment();
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null)
            initialEventId = arguments.getLong(EVENT_ID);
        eventDashboardViewModel = ViewModelProviders.of(this).get(EventDashboardViewModel.class);

        if (eventDashboardViewModel.getEventId() == null) {
            eventDashboardViewModel.setEventId(initialEventId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventDetailBinding.inflate(inflater, container, false);

        binding.ticketAnalytics.btnChartFullScreen.setOnClickListener(
            v -> {
                Intent openChart = new Intent(getActivity(), ChartActivity.class);
                openChart.putExtra(EVENT_ID, eventDashboardViewModel.getEventId());
                startActivity(openChart);
            });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(eventDashboardViewModel.getEventId(), this);
        binding.setPresenter(getPresenter());
        setupRefreshListener();
        getPresenter().start();
    }

    @Override
    protected int getTitle() {
        return R.string.dashboard;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    @Override
    public Lazy<IEventDashboardPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.fragment_event_details;
    }

    private void setupRefreshListener() {
        container = binding.container;
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() ->
            getPresenter().loadDetails(true)
        );
    }

    // View implementation

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(container, R.string.refresh_complete);
    }

    @Override
    public void showResult(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public LineChart getChartView() {
        return binding.ticketAnalytics.chart;
    }

    @Override
    public void showChart(boolean show) {
        ViewUtils.showView(binding.ticketAnalytics.chartBox, show);
    }
}
