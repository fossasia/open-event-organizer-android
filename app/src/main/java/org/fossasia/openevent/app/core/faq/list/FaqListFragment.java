package org.fossasia.openevent.app.core.faq.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.faq.create.CreateFaqFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.databinding.FaqsFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@SuppressWarnings("PMD.TooManyMethods")
public class FaqListFragment extends BaseFragment<FaqListPresenter> implements IFaqListView {

    private Context context;
    private long eventId;
    private boolean deletingMode;
    private AlertDialog deleteDialog;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<FaqListPresenter> faqsPresenter;

    private FaqListAdapter faqsAdapter;
    private FaqsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private boolean initialized;

    public static FaqListFragment newInstance(long eventId) {
        FaqListFragment fragment = new FaqListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deletingMode = false;
        context = getContext();
        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.faqs_fragment, container, false);

        binding.createFaqFab.setOnClickListener(view -> {
            getPresenter().resetToDefaultState();
            BottomSheetDialogFragment bottomSheetDialogFragment = CreateFaqFragment.newInstance();
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
        });

        return binding.getRoot();
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
        return R.string.faq;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        if (!initialized) {
            faqsAdapter = new FaqListAdapter(getPresenter());

            RecyclerView recyclerView = binding.faqsRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(faqsAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadFaqs(true);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.del:
                showDeleteDialog();
                break;
            default:
                // No implementation
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.del);
        menuItem.setVisible(deletingMode);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_faqs, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    getPresenter().deleteSelectedFaq();
                    resetToolbar();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss(); getPresenter().resetToDefaultState();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void changeToDeletingMode() {
        deletingMode = true;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void resetToolbar() {
        deletingMode = false;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public Lazy<FaqListPresenter> getPresenterProvider() {
        return faqsPresenter;
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
        resetToolbar();
        if (success)
            ViewUtils.showSnackbar(binding.faqsRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.faqsRecyclerView, message);
    }

    @Override
    public void showResults(List<Faq> items) {
        faqsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

}
