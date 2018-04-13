package org.fossasia.openevent.app.core.event.about;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightFragment;
import org.fossasia.openevent.app.core.event.copyright.update.UpdateCopyrightFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.copyright.Copyright;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.AboutEventFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.disposables.CompositeDisposable;

public class AboutEventFragment extends BaseFragment<AboutEventPresenter> implements AboutEventVew {

    private AboutEventFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private long eventId;
    private static final String EVENT_ID = "id";
    private boolean creatingCopyright = true;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    Lazy<AboutEventPresenter> aboutEventPresenterProvider;
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
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        toolbarColorChanger.removeChangeListener();
        compositeDisposable.dispose();
    }

    private void observeColorChanges() {
        Drawable icon = binding.toolbar.getNavigationIcon();
        Drawable overflowIcon = binding.toolbar.getOverflowIcon();
        compositeDisposable.add(toolbarColorChanger.observeColor(binding.appBar, binding.collapsingToolbar)
            .subscribe(color -> {
                for (Drawable drawable : Arrays.asList(icon, overflowIcon)) {
                    if (drawable != null) drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            }));
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadEvent(true);
            getPresenter().loadCopyright(true);
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
            getPresenter().deleteCopyright(getPresenter().getCopyright().getId());
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
    public Lazy<AboutEventPresenter> getPresenterProvider() {
        return aboutEventPresenterProvider;
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
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }

    @Override
    public void showCopyrightDeleted(String message) {
        ViewUtils.showSnackbar(binding.mainContent, message);
    }
}
