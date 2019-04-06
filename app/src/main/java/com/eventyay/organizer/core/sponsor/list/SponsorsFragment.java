package com.eventyay.organizer.core.sponsor.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.eventyay.organizer.core.sponsor.create.CreateSponsorFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.databinding.SponsorsFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@SuppressWarnings("PMD.TooManyMethods")
public class SponsorsFragment extends BaseFragment<SponsorsPresenter> implements SponsorsView {

    private Context context;
    private boolean deletingMode;
    private boolean editMode;
    private long eventId;
    private AlertDialog deleteDialog;
    private ActionMode actionMode;
    private int statusBarColor;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<SponsorsPresenter> sponsorsPresenter;

    private SponsorsListAdapter sponsorsListAdapter;
    private SponsorsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

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
        binding.createSponsorFab.setOnClickListener(view -> {
            openCreateSponsorFragment();
        });

        if (!getPresenter().getSponsors().isEmpty()) {
            getPresenter().loadSponsors(false);
        }

        return binding.getRoot();
    }

    public void openCreateSponsorFragment() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateSponsorFragment.newInstance())
            .addToBackStack(null)
            .commit();
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
        return R.string.sponsors;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        sponsorsListAdapter = new SponsorsListAdapter(getPresenter());

        RecyclerView recyclerView = binding.sponsorsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(sponsorsListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.createSponsorFab);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadSponsors(true);
        });
    }

    public ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_sponsors, menu);

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
            menuItemEdit.setVisible(editMode);
            menuItemDelete.setVisible(deletingMode);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.del:
                    showDeleteDialog();
                    break;
                case R.id.edit:
                    getPresenter().updateSponsor();
                    break;
                default:
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode.finish();
            getPresenter().unselectSponsorsList();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
        }
    };

    @Override
    public void enterContextualMenuMode() {
        actionMode = getActivity().startActionMode(actionCallback);
    }

    @Override
    public void exitContextualMenuMode() {
        if (actionMode != null)
            actionMode.finish();
    }

    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.session)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteSelectedSponsors();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void changeToolbarMode(boolean editMode, boolean deleteMode) {
        this.editMode = editMode;
        this.deletingMode = deleteMode;

        if (actionMode != null) {
            actionMode.invalidate();
        }
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
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void openUpdateSponsorFragment(long sponsorId) {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateSponsorFragment.newInstance(sponsorId))
            .addToBackStack(null)
            .commit();
    }
}
