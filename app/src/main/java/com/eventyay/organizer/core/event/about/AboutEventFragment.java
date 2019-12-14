package com.eventyay.organizer.core.event.about;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.event.copyright.CreateCopyrightFragment;
import com.eventyay.organizer.core.event.copyright.update.UpdateCopyrightFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.databinding.AboutEventFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.Arrays;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@SuppressWarnings("PMD.TooManyMethods")
public class AboutEventFragment extends BaseFragment implements AboutEventView {

    private AboutEventFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private long eventId;
    private static final String EVENT_ID = "id";
    private boolean creatingCopyright = true;
    private AlertDialog deleteDialog;
    private Drawable shareIcon;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private AboutEventViewModel aboutEventViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    ContextUtils utilModel;
    @Inject
    ToolbarColorChanger toolbarColorChanger;

    public static AboutEventFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(EVENT_ID, id);
        AboutEventFragment aboutEventFragment = new AboutEventFragment();
        aboutEventFragment.setArguments(bundle);
        return aboutEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.about_event_fragment, container, false);
        aboutEventViewModel = ViewModelProviders.of(this, viewModelFactory).get(AboutEventViewModel.class);

        Bundle bundle = getArguments();
        eventId = bundle.getLong(EVENT_ID);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        handleVisibility();
        addCopyrightListeners();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        observeColorChanges();
        setupRefreshListener();
        aboutEventViewModel.getProgress().observe(this, this::showProgress);
        aboutEventViewModel.getSuccess().observe(this, this::showResult);
        aboutEventViewModel.getError().observe(this, this::showError);
        aboutEventViewModel.getShowCopyright().observe(this, this::showCopyright);
        aboutEventViewModel.getChangeCopyrightMenuItem().observe(this, this::changeCopyrightMenuItem);
        aboutEventViewModel.getShowCopyrightDeleted().observe(this, this::showCopyrightDeleted);
        aboutEventViewModel.loadEvent(false);
        aboutEventViewModel.loadCopyright(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        toolbarColorChanger.removeChangeListener();
        compositeDisposable.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_event:
                shareEvent();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_share_event);
        shareIcon = menu.findItem(R.id.action_share_event).getIcon();
        shareIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        menuItem.setVisible(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void shareEvent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, aboutEventViewModel.getShareableInformation());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    private void observeColorChanges() {
        Drawable icon = binding.toolbar.getNavigationIcon();
        Drawable overflowIcon = binding.toolbar.getOverflowIcon();

        compositeDisposable.add(toolbarColorChanger.observeColor(binding.appBar, binding.collapsingToolbar)
            .subscribe(color -> {
                for (Drawable drawable : Arrays.asList(icon, overflowIcon, shareIcon)) {
                    if (drawable != null) drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            }));
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            aboutEventViewModel.loadEvent(true);
            aboutEventViewModel.loadCopyright(true);
        });
    }

    private void addCopyrightListeners() {
        binding.detail.actionCreateCopyright.setOnClickListener(view -> {
            getFragmentManager().beginTransaction()
                .add(R.id.fragment, CreateCopyrightFragment.newInstance())
                .addToBackStack(null)
                .commit();
        });

        binding.detail.actionChangeCopyright.setOnClickListener(view -> {
            getFragmentManager().beginTransaction()
                .add(R.id.fragment, UpdateCopyrightFragment.newInstance(eventId))
                .addToBackStack(null)
                .commit();
        });

        binding.detail.actionDeleteCopyright.setOnClickListener(view -> {
            if (deleteDialog == null)
                deleteDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete)
                    .setMessage(String.format(getString(R.string.delete_confirmation_message),
                        getString(R.string.copyright)))
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        aboutEventViewModel.deleteCopyright(aboutEventViewModel.getCopyright().getId());
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();

            deleteDialog.show();
        });
    }

    public void handleVisibility() {
        if (creatingCopyright) {
            binding.detail.actionChangeCopyright.setVisibility(View.GONE);
            binding.detail.actionDeleteCopyright.setVisibility(View.GONE);
            binding.detail.actionCreateCopyright.setVisibility(View.VISIBLE);
        } else {
            binding.detail.actionChangeCopyright.setVisibility(View.VISIBLE);
            binding.detail.actionDeleteCopyright.setVisibility(View.VISIBLE);
            binding.detail.actionCreateCopyright.setVisibility(View.GONE);
        }
    }

    @Override
    public void setEventId(long id) {
        this.eventId = id;
    }

    @Override
    protected int getTitle() {
        return R.string.about;
    }

    @Override
    public void showResult(Event item) {
        binding.setEvent(item);
    }

    @Override
    public void showCopyright(Copyright copyright) {
        binding.setCopyright(copyright);
    }

    @Override
    public void changeCopyrightMenuItem(boolean creatingCopyright) {
        this.creatingCopyright = creatingCopyright;
        handleVisibility();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.mainContent, error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showCopyrightDeleted(String message) {
        ViewUtils.showSnackbar(binding.mainContent, message);
    }
}
