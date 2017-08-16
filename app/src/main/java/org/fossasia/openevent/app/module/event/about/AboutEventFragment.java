package org.fossasia.openevent.app.module.event.about;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.AboutEventFragmentBinding;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.Observable;

public class AboutEventFragment extends BaseFragment<IAboutEventPresenter> implements IAboutEventVew {

    private AboutEventFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private long eventId;

    @Inject
    Lazy<IAboutEventPresenter> aboutEventPresenterProvider;
    @Inject
    IUtilModel utilModel;

    public AboutEventFragment() {
        OrgaApplication.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.about_event_fragment, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                Observable.just((binding.collapsingToolbar.getHeight() + verticalOffset) <
                    (2 * ViewCompat.getMinimumHeight(binding.collapsingToolbar)))
                    .distinctUntilChanged()
                    .map(collapsed -> {
                        if (collapsed)
                            return getResources().getColor(android.R.color.black);
                        else
                            return getResources().getColor(android.R.color.white);
                    }).subscribe(color -> {
                    Drawable icon = binding.toolbar.getNavigationIcon();
                    if (icon != null) icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }));
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> getPresenter().loadEvent(true));
    }

    @Override
    public void setEventId(long id) {
        this.eventId = id;
    }

    @Override
    public Lazy<IAboutEventPresenter> getPresenterProvider() {
        return aboutEventPresenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.about_event_fragment;
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
}
