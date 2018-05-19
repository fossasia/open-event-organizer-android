package org.fossasia.openevent.app.core.session.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.session.create.CreateSessionFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SessionsFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class SessionsFragment extends BaseFragment<SessionsPresenter> implements SessionsView {

    public static final String TRACK_KEY = "track";
    private Context context;
    private long trackId;
    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<SessionsPresenter> sessionsPresenter;

    private SessionsAdapter sessionsAdapter;
    private SessionsFragmentBinding binding;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout refreshLayout;

    public static SessionsFragment newInstance(long trackId, long eventId) {
        SessionsFragment fragment = new SessionsFragment();
        Bundle args = new Bundle();
        args.putLong(TRACK_KEY, trackId);
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        if (getArguments() != null) {
            trackId = getArguments().getLong(TRACK_KEY);
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sessions_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(trackId, this);
        getPresenter().start();

        binding.createSessionFab.setOnClickListener(view -> openCreateSessionFragment());
    }

    public void openCreateSessionFragment() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateSessionFragment.newInstance(trackId, eventId))
            .addToBackStack(null)
            .commit();
    }

    @Override
    protected int getTitle() {
        return R.string.session;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        sessionsAdapter = new SessionsAdapter(getPresenter());

        RecyclerView recyclerView = binding.sessionsRecyclerView;
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
        recyclerView.setAdapter(sessionsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadSessions(true);
        });
    }

    @Override
    public Lazy<SessionsPresenter> getPresenterProvider() {
        return sessionsPresenter;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(binding.sessionsRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Session> items) {
        sessionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }
}
