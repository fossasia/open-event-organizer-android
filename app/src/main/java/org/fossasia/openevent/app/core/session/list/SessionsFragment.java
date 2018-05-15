package org.fossasia.openevent.app.core.session.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

@SuppressWarnings("PMD.TooManyMethods")
public class SessionsFragment extends BaseFragment<SessionsPresenter> implements SessionsView {

    public static final String TRACK_KEY = "track";
    private Context context;
    private long trackId;
    private long eventId;
    private boolean deletingMode;
    private AlertDialog deleteDialog;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<SessionsPresenter> sessionsPresenter;

    private SessionsAdapter sessionsAdapter;
    private SessionsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

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

        deletingMode = false;
        context = getContext();
        setHasOptionsMenu(true);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(sessionsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            resetToolbar();
            getPresenter().loadSessions(true);
        });
    }

    @Override
    public Lazy<SessionsPresenter> getPresenterProvider() {
        return sessionsPresenter;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.del:
                showDeleteDialog();
                break;
            default:
                // No implementation
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.del);
        menuItem.setVisible(deletingMode);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sessions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.session)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteSelectedSessions();
                    resetToolbar();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss(); getPresenter().resetToDefaultState();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void changeToDeletingMode() {
        deletingMode = true;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void resetToolbar() {
        deletingMode = false;
        getActivity().invalidateOptionsMenu();
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
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
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
