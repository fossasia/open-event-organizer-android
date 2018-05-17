package org.fossasia.openevent.app.core.sponsor.list;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import org.fossasia.openevent.app.core.sponsor.create.CreateSponsorFragment;
import org.fossasia.openevent.app.core.sponsor.update.UpdateSponsorFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorsFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class SponsorsFragment extends BaseFragment<SponsorsPresenter> implements SponsorsView {

    private Context context;
    private long eventId;
    private AlertDialog deleteDialog;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<SponsorsPresenter> sponsorsPresenter;

    private SponsorsListAdapter sponsorsListAdapter;
    private SponsorsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private boolean initialized;

    public static SponsorsFragment newInstance(long eventId) {
        SponsorsFragment fragment = new SponsorsFragment();
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sponsors_fragment, container, false);
        binding.createSponsorFab.setOnClickListener(view -> openCreateSponsorFragment());
        return binding.getRoot();
    }

    public void openCreateSponsorFragment() {

        BottomSheetDialogFragment bottomSheetDialogFragment = CreateSponsorFragment.newInstance();
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
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
        return R.string.sponsors;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        if (!initialized) {
            sponsorsListAdapter = new SponsorsListAdapter(getPresenter());

            RecyclerView recyclerView = binding.sponsorsRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(sponsorsListAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.createSponsorFab);
        }
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadSponsors(true);
        });
    }

    @Override
    public Lazy<SponsorsPresenter> getPresenterProvider() {
        return sponsorsPresenter;
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
            ViewUtils.showSnackbar(binding.sponsorsRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Sponsor> items) {
        sponsorsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void showSponsorDeleted(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void openUpdateSponsorFragment(long sponsorId) {
        BottomSheetDialogFragment bottomSheetDialogFragment = UpdateSponsorFragment.newInstance(sponsorId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void showAlertDialog(long sponsorId) {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.sponsors)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteSponsor(sponsorId);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
    }
}
