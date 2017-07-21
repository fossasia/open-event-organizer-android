package org.fossasia.openevent.app.event.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.lifecycle.BaseFragment;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventDetailBinding;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
import org.fossasia.openevent.app.utils.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment<IEventDetailPresenter> implements IEventDetailView {

    private static final String EVENT_ID = "event_id";

    private long initialEventId;
    private EventDetailBinding binding;

    @Inject
    Context context;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<IEventDetailPresenter> presenterProvider;

    private CoordinatorLayout container;
    private SwipeRefreshLayout refreshLayout;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId Event for which the Fragment is to be created.
     * @return A new instance of fragment EventDetailFragment.
     */
    public static EventDetailFragment newInstance(long eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(getActivity())
            .inject(this);

        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null)
            initialEventId = arguments.getLong(EVENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(initialEventId, this);
        setupRefreshListener();
        getPresenter().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    @Override
    public Lazy<IEventDetailPresenter> getPresenterProvider() {
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
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(container, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public LineChart getChartView() {
        return binding.ticketAnalytics.chart;
    }
}
