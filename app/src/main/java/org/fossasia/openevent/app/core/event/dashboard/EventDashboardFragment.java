package org.fossasia.openevent.app.core.event.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.chart.ChartActivity;
import org.fossasia.openevent.app.core.event.create.CreateEventActivity;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventStatistics;
import org.fossasia.openevent.app.data.order.OrderStatistics;
import org.fossasia.openevent.app.databinding.EventDetailBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.Utils;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDashboardFragment extends BaseFragment<EventDashboardPresenter> implements EventDashboardView {

    public static final String EVENT_ID = "event_id";

    private long initialEventId;
    private EventDetailBinding binding;
    private AlertDialog unpublishDialog;

    @Inject
    Context context;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<EventDashboardPresenter> presenterProvider;

    private ConstraintLayout container;
    private SwipeRefreshLayout refreshLayout;

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
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null)
            initialEventId = arguments.getLong(EVENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventDetailBinding.inflate(inflater, container, false);

        binding.ticketAnalytics.btnChartFullScreen.setOnClickListener(
            v -> {
                Intent openChart = new Intent(getActivity(), ChartActivity.class);
                openChart.putExtra(EVENT_ID, initialEventId);
                startActivity(openChart);
            });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(initialEventId, this);
        binding.eventStatistics.switchEventStatistics.setChecked(false);
        binding.orderStatistics.switchOrderStatistics.setChecked(false);
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
    public Lazy<EventDashboardPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    private void setupRefreshListener() {
        container = binding.container;
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadDetails(true);
        });
    }

    // View implementation

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(container, R.string.refresh_complete);
    }

    @Override
    public void showResult(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    @Override
    public void showStatistics(EventStatistics eventStatistics) {
        binding.setEventStats(eventStatistics);
        binding.executePendingBindings();
    }

    @Override
    public void showOrderStatistics(OrderStatistics orderStatistics) {
        binding.setOrderStats(orderStatistics);
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
    public LineChart getSalesChartView() {
        return binding.ticketAnalytics.chartSales;
    }

    @Override
    public LineChart getCheckinTimeChartView() {
        return binding.ticketAnalytics.chartCheckIn;
    }

    @Override
    public void showChartSales(boolean show) {
        ViewUtils.showView(binding.ticketAnalytics.chartBox, show);
    }

    @Override
    public void showChartCheckIn(boolean show) {
        ViewUtils.showView(binding.ticketAnalytics.chartBoxCheckIn, show);
    }

    public void shareEvent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Utils.getShareableInformation(getPresenter().getEvent()));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    public void openEditEvent() {
        Intent intent = new Intent(context, CreateEventActivity.class);
        intent.putExtra(EVENT_ID, initialEventId);
        context.startActivity(intent);
    }

    public void showEventLocationDialog() {
        ViewUtils.showDialog(this, getString(R.string.event_location),
            getString(R.string.event_location_required), getString(R.string.add_location), this::openEditEvent);
    }

    public void showEventShareDialog() {
        ViewUtils.showDialog(this, getString(R.string.share_event),
            getString(R.string.successfull_publish_message), getString(R.string.share), this::shareEvent);
    }

    public void switchEventState() {
        binding.switchEventState.toggle();
    }

    @Override
    public void showEventUnpublishDialog() {
        if (unpublishDialog == null) {
            unpublishDialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialog))
                .setTitle(R.string.unpublish_event)
                .setMessage(getString(R.string.unpublish_confirmation_message))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().toggleState();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
        }

        unpublishDialog.show();
    }
}
