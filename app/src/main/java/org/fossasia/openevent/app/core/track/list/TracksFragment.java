package org.fossasia.openevent.app.core.track.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.track.create.CreateTrackFragment;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TracksFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class TracksFragment extends BaseFragment<TracksPresenter> implements TracksView {
    private Context context;
    private long eventId;

    @Inject
    Lazy<TracksPresenter> tracksPresenter;

    private TracksAdapter tracksAdapter;
    private TracksFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private boolean initialized;

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
            BottomSheetDialogFragment bottomSheetDialogFragment = CreateTrackFragment.newInstance();
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

        initialized = true;
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
        if (!initialized) {
            tracksAdapter = new TracksAdapter(getPresenter());

            RecyclerView recyclerView = binding.tracksRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(tracksAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadTracks(true);
        });
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
}
