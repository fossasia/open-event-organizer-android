package com.eventyay.organizer.core.event.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.event.chart.ChartActivity;
import com.eventyay.organizer.core.event.create.CreateEventActivity;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventStatistics;
import com.eventyay.organizer.data.order.OrderStatistics;
import com.eventyay.organizer.databinding.EventDetailBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.Utils;

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
    private AlertDialog shareEventDialog;

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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void showEventLocationDialog() {
        ViewUtils.showDialog(this, getString(R.string.event_location),
            getString(R.string.event_location_required), getString(R.string.add_location), this::openEditEvent);
    }

    public void showEventShareDialog() {
       if (shareEventDialog == null) {
            shareEventDialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialog))
                .setTitle(R.string.share_event)
                .setMessage(getString(R.string.successfull_publish_message))
                .setPositiveButton(R.string.share, (dialog, which) -> {
                    shareEvent();
                })
                .setNegativeButton(R.string.not_now, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
        }

        shareEventDialog.show();
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
