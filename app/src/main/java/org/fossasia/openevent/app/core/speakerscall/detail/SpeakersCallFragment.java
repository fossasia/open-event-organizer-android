package org.fossasia.openevent.app.core.speakerscall.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.speakerscall.create.CreateSpeakersCallFragment;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.databinding.SpeakersCallFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class SpeakersCallFragment extends BaseFragment<SpeakersCallPresenter> implements SpeakersCallView {

    private long eventId;

    @Inject
    Lazy<SpeakersCallPresenter> speakersCallPresenter;

    private SpeakersCallFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private boolean editMode;

    public static SpeakersCallFragment newInstance(long eventId) {
        SpeakersCallFragment fragment = new SpeakersCallFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.speakers_call_fragment, container, false);
        binding.createSpeakersCallFab.setOnClickListener(view -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = CreateSpeakersCallFragment.newInstance(eventId);
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();

        if (getPresenter().getSpeakersCall() != null) {
            editMode = true;
        }
    }

    @Override
    protected int getTitle() {
        return R.string.speakers_call;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadSpeakersCall(true);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                BottomSheetDialogFragment bottomSheetDialogFragment = CreateSpeakersCallFragment.newInstance(eventId, true);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                break;
            default:
                // No implementation
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemEdit = menu.findItem(R.id.edit);
        menuItemEdit.setVisible(editMode);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_speakers_call, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Lazy<SpeakersCallPresenter> getPresenterProvider() {
        return speakersCallPresenter;
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
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
    }

    @Override
    public void showResult(SpeakersCall speakersCall) {
        if (speakersCall == null) {
            editMode = false;
            ViewUtils.showView(binding.emptyView, true);
            binding.fabFrame.setVisibility(View.VISIBLE);
            return;
        }

        ViewUtils.showView(binding.emptyView, false);
        editMode = true;
        getActivity().invalidateOptionsMenu();
        binding.setSpeakersCall(speakersCall);
        binding.executePendingBindings();
    }
}
