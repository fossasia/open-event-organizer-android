package org.fossasia.openevent.app.module.organizer.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.OrganizerDetailFragmentBinding;
import org.fossasia.openevent.app.module.organizer.detail.contract.IOrganizerDetailPresenter;
import org.fossasia.openevent.app.module.organizer.detail.contract.IOrganizerDetailView;

import javax.inject.Inject;

import dagger.Lazy;

public class OrganizerDetailFragment extends BaseFragment<IOrganizerDetailPresenter> implements IOrganizerDetailView {

    private OrganizerDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    @Inject
    IUtilModel utilModel;
    @Inject
    Lazy<IOrganizerDetailPresenter> presenterProvider;

    public OrganizerDetailFragment() {
        OrgaApplication.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.organizer_detail_fragment, container, false);

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
        setupRefreshListener();
        getPresenter().attach(this);
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
        refreshLayout.setOnRefreshListener(() -> getPresenter().loadOrganizer(true));
    }

    @Override
    public Lazy<IOrganizerDetailPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.organizer_detail_fragment;
    }

    @Override
    protected int getTitle() {
        return R.string.title_activity_organizer_detail;
    }

    @Override
    public void showResult(User item) {
        binding.setUser(item);
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
