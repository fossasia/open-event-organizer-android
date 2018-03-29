package org.fossasia.openevent.app.core.event.about;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightFragment;
import org.fossasia.openevent.app.core.event.copyright.update.UpdateCopyrightFragment;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.AboutEventFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.disposables.CompositeDisposable;

public class AboutEventFragment extends BaseFragment<AboutEventPresenter> implements IAboutEventVew {

    private AboutEventFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private long eventId;
    private boolean creatingCopyright = true;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    Lazy<AboutEventPresenter> aboutEventPresenterProvider;
    @Inject
    IUtilModel utilModel;
    @Inject
    ToolbarColorChanger toolbarColorChanger;

    public AboutEventFragment() {
        OrgaApplication.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.about_event_fragment, container, false);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_about_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_change_copyright:
                if (creatingCopyright) {
                    BottomSheetDialogFragment bottomSheetDialogFragment = CreateCopyrightFragment.newInstance();
                    bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                } else {
                    BottomSheetDialogFragment bottomSheetDialogFragment = UpdateCopyrightFragment.newInstance(getPresenter().getCopyright());
                    bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                }
                break;
            case R.id.action_delete_copyright:
                getPresenter().deleteCopyright(getPresenter().getCopyright().getId());
                break;
            default:
                // No implementation
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (creatingCopyright) {
            MenuItem menuItem = menu.findItem(R.id.action_create_change_copyright);
            menuItem.setTitle(R.string.create_copyright);
            menuItem = menu.findItem(R.id.action_delete_copyright);
            menuItem.setVisible(false);
        } else {
            MenuItem menuItem = menu.findItem(R.id.action_create_change_copyright);
            menuItem.setTitle(R.string.edit_copyright);
            menuItem = menu.findItem(R.id.action_delete_copyright);
            menuItem.setVisible(true);
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
        getActivity().invalidateOptionsMenu();
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
