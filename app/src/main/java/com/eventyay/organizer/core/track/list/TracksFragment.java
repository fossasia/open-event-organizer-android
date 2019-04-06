package com.eventyay.organizer.core.track.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.session.list.SessionsFragment;
import com.eventyay.organizer.core.track.create.CreateTrackFragment;
import com.eventyay.organizer.core.track.update.UpdateTrackFragment;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.databinding.TracksFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@SuppressWarnings("PMD.TooManyMethods")
public class TracksFragment extends BaseFragment<TracksPresenter> implements TracksView {
    private Context context;
    private boolean toolbarEdit;
    private boolean toolbarDelete;
    private long eventId;
    private AlertDialog deleteDialog;
    private ActionMode actionMode;
    private int statusBarColor;

    @Inject
    Lazy<TracksPresenter> tracksPresenter;

    private TracksAdapter tracksAdapter;
    private TracksFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static TracksFragment newInstance(long eventId) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracks_fragment, container, false);

        binding.createTrackFab.setOnClickListener(view -> {
            openCreateTrackFragment();
        });

        if (!getPresenter().getTracks().isEmpty()) {
            getPresenter().loadTracks(false);
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    public void openCreateTrackFragment() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateTrackFragment.newInstance())
            .addToBackStack(null)
            .commit();
    }

    public ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_tracks, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                //set the default color
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.color_top_surface));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem menuItemDelete = menu.findItem(R.id.del);
            MenuItem menuItemEdit = menu.findItem(R.id.edit);
            menuItemEdit.setVisible(toolbarEdit);
            menuItemDelete.setVisible(toolbarDelete);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.del:
                    showDeleteDialog();
                    break;
                case R.id.edit:
                    getPresenter().updateTrack();
                    break;
                default:
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode.finish();
            getPresenter().resetToolbarToDefaultState();
            getPresenter().unselectTracksList();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
        }
    };

    @Override
    protected int getTitle() {
        return R.string.tracks;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        tracksAdapter = new TracksAdapter(getPresenter());

        RecyclerView recyclerView = binding.tracksRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(tracksAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadTracks(true);
        });
    }

    public void openSessionsFragment(long trackId) {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, SessionsFragment.newInstance(trackId, eventId))
            .addToBackStack(null)
            .commit();
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
            ViewUtils.showSnackbar(binding.tracksRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Track> items) {
        tracksAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.tracks)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteSelectedTracks();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void changeToolbarMode(boolean toolbarEdit, boolean toolbarDelete) {
        this.toolbarEdit = toolbarEdit;
        this.toolbarDelete = toolbarDelete;
        if (actionMode != null)
            actionMode.invalidate();
    }

    @Override
    public void exitContextualMenuMode() {
        if (actionMode != null)
            actionMode.finish();
    }

    @Override
    public void enterContextualMenuMode() {
        actionMode = getActivity().startActionMode(actionCallback);
    }

    @Override
    protected Lazy<TracksPresenter> getPresenterProvider() {
        return tracksPresenter;
    }

    @Override
    public void openUpdateTrackFragment(long trackId) {
        BottomSheetDialogFragment bottomSheetDialogFragment = UpdateTrackFragment.newInstance(trackId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
