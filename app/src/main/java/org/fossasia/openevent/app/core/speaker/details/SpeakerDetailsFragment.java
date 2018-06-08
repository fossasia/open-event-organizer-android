package org.fossasia.openevent.app.core.speaker.details;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.speaker.Speaker;
import org.fossasia.openevent.app.databinding.SpeakerDetailsFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class SpeakerDetailsFragment extends BaseFragment implements SpeakerDetailsView {

    private long speakerId;
    private Context context;
    private static final String SPEAKER_ID = "speaker_id";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SpeakerDetailsViewModel speakerDetailsViewModel;
    private SpeakerDetailsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private SpeakersSessionsAdapter speakersSessionsAdapter;

    public static SpeakerDetailsFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(SPEAKER_ID, id);
        SpeakerDetailsFragment speakerDetailsFragment = new SpeakerDetailsFragment();
        speakerDetailsFragment.setArguments(bundle);
        return speakerDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        binding = DataBindingUtil.inflate(inflater, R.layout.speaker_details_fragment, container, false);
        speakerDetailsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SpeakerDetailsViewModel.class);
        speakerId = getArguments().getLong(SPEAKER_ID);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        speakerDetailsViewModel.getProgress().observe(this, this::showProgress);
        speakerDetailsViewModel.getError().observe(this, this::showError);
        loadSpeaker(false);

        setupRecyclerView();
        setupRefreshListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        speakersSessionsAdapter = new SpeakersSessionsAdapter();

        RecyclerView recyclerView = binding.detail.sessionsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(speakersSessionsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            loadSpeaker(true);
        });
    }

    private void loadSpeaker(boolean reload) {
        speakerDetailsViewModel.getSpeaker(speakerId, reload).observe(this, this::showResult);
        speakerDetailsViewModel.getSessionsUnderSpeaker().observe(this, this::showSessions);
    }

    @Override
    protected int getTitle() {
        return R.string.speaker_details;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showResult(Speaker item) {
        binding.setSpeaker(item);
    }

    @Override
    public void showSessions(List<Session> sessions) {
        speakersSessionsAdapter.setSessions(sessions);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }
}
