package com.eventyay.organizer.core.organizer.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.organizer.password.ChangePasswordFragment;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.OrganizerDetailFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class OrganizerDetailFragment extends BaseFragment<OrganizerDetailPresenter> implements OrganizerDetailView {

    private OrganizerDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    @Inject
    ContextUtils utilModel;
    @Inject
    Lazy<OrganizerDetailPresenter> presenterProvider;
    public static final String INFO_FRAGMENT_TAG = "info";

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

        setHasOptionsMenu(true);

        binding.detail.resendVerificationMail.setOnClickListener(view -> {
            getPresenter().resendVerificationMail();
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_organizer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_change_password:
                openChangePasswordFragment();
                break;
            case R.id.update_organizer:
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, UpdateOrganizerInfoFragment.newInstance(), INFO_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChangePasswordFragment() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment, new ChangePasswordFragment())
            .addToBackStack(null)
            .commit();
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadOrganizer(true);
        });
    }

    @Override
    public Lazy<OrganizerDetailPresenter> getPresenterProvider() {
        return presenterProvider;
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
    public void showSnackbar(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }
}
