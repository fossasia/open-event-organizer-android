package org.fossasia.openevent.app.module.event.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.FragmentEventListBinding;
import org.fossasia.openevent.app.module.event.list.contract.IEventsPresenter;
import org.fossasia.openevent.app.module.event.list.contract.IEventsView;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends BaseFragment<IEventsPresenter> implements IEventsView {
    @Inject
    IUtilModel utilModel;

    @Inject
    IBus bus;

    @Inject
    Lazy<IEventsPresenter> presenterProvider;

    private FragmentEventListBinding binding;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private EventsListAdapter eventListAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    private Context context;
    private boolean initialized;

    public EventListFragment() {
        OrgaApplication
            .getAppComponent()
            .inject(this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * parameters can be added in future if required so
     * which can be passed in bundle.
     *
     * @return A new instance of fragment EventListFragment.
     */
    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_list, container, false);
        return binding.getRoot();
    }

    @Override
    public Lazy<IEventsPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.fragment_event_list;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(this);
        binding.setEvents(getPresenter().getEvents());
        getPresenter().start();

        initialized = true;
    }

    @Override
    protected int getTitle() {
        return R.string.events;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        eventListAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    private void setupRecyclerView() {
        if (!initialized) {
            eventListAdapter = new EventsListAdapter(getPresenter().getEvents(), bus);

            recyclerView = binding.eventRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(eventListAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(eventListAdapter);
            recyclerView.addItemDecoration(decoration);

            adapterDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    decoration.invalidateHeaders();
                }
            };
        }

        eventListAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> getPresenter().loadUserEvents(true));
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(recyclerView, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResults(List<Event> events) {
        binding.setVariable(BR.events, events);
        binding.executePendingBindings();
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void showOrganiserName(String name) {
        // TODO: Decide where to show organizer's name and implement accordingly.
    }

    @Override
    public void showError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOrganiserLoadError(String error) {
        Timber.d(error);
    }
}
