package org.fossasia.openevent.app.core.track.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.session.list.SessionsFragment;
import org.fossasia.openevent.app.core.track.create.CreateTrackFragment;
import org.fossasia.openevent.app.core.track.update.UpdateTrackFragment;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TracksFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class TracksFragment extends BaseFragment<TracksPresenter> implements TracksView {
    private Context context;
    private long eventId;
    private AlertDialog deleteDialog;

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
        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracks_fragment, container, false);

        binding.createTrackFab.setOnClickListener(view -> {
            com.google.android.material.bottomsheet.BottomSheetDialogFragment bottomSheetDialogFragment = CreateTrackFragment.newInstance();
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
        });

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
        recyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
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
    public void showAlertDialog(long trackId) {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.track)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteTrack(trackId);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
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
    protected Lazy<TracksPresenter> getPresenterProvider() {
        return tracksPresenter;
    }

    @Override
    public void openUpdateTrackFragment(long trackId) {
        com.google.android.material.bottomsheet.BottomSheetDialogFragment bottomSheetDialogFragment = UpdateTrackFragment.newInstance(trackId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void showTrackDeleted(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
