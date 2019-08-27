package com.eventyay.organizer.core.speaker.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.speaker.details.SpeakerDetailsActivity;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.databinding.SpeakersFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class SpeakersFragment extends BaseFragment<SpeakersPresenter> implements SpeakersView {
    private Context context;
    private long eventId;

    @Inject Lazy<SpeakersPresenter> speakersPresenter;

    private SpeakersAdapter speakersAdapter;
    private SpeakersFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static SpeakersFragment newInstance(long eventId) {
        SpeakersFragment fragment = new SpeakersFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        if (getArguments() != null) eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.speakers_fragment, container, false);

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
        return R.string.speakers;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        speakersAdapter = new SpeakersAdapter(getPresenter());

        RecyclerView recyclerView = binding.speakersRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(speakersAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(
                () -> {
                    refreshLayout.setRefreshing(false);
                    getPresenter().loadSpeakers(true);
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
        // Nothing to do
    }

    @Override
    public void showResults(List<Speaker> items) {
        speakersAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    protected Lazy<SpeakersPresenter> getPresenterProvider() {
        return speakersPresenter;
    }

    @Override
    public void openSpeakersDetailFragment(long speakerId) {
        Intent intent = new Intent(context, SpeakerDetailsActivity.class);
        intent.putExtra(SpeakerDetailsActivity.SPEAKER_ID, speakerId);
        context.startActivity(intent);
    }
}
