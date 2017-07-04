package org.fossasia.openevent.app.event.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseFragment;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventDetailBinding;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
import org.fossasia.openevent.app.main.listeners.OnEventLoadedListener;
import org.fossasia.openevent.app.utils.ViewUtils;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment implements IEventDetailView {

    private static final String EVENT_ID = "event_id";

    private long initialEventId;
    private EventDetailBinding binding;

    @Inject
    Context context;

    @Inject
    IUtilModel utilModel;

    @Inject
    IEventDetailPresenter eventDetailPresenter;

    private OnEventLoadedListener listener;

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

        eventDetailPresenter.attach(this, initialEventId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventDetailBinding.inflate(inflater, container, false);
        setupRefreshListener();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventDetailPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventLoadedListener) {
            listener = (OnEventLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnEventLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventDetailPresenter.detach();
        listener = null;
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRefreshListener() {
        container = binding.container;
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() ->
            eventDetailPresenter.refresh()
        );
    }

    // View implementation

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(container, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showEvent(Event event) {
        listener.onEventLoaded(event, false);
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    @Override
    public void showEventLoadError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
