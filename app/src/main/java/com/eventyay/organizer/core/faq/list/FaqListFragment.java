package com.eventyay.organizer.core.faq.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.faq.create.CreateFaqFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.databinding.FaqsFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("PMD.TooManyMethods")
public class FaqListFragment extends BaseFragment implements FaqListView {

    private Context context;
    private long eventId;
    private AlertDialog deleteDialog;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FaqListAdapter faqsAdapter;
    private FaqsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private ActionMode actionMode;
    private int statusBarColor;

    private FaqListViewModel faqListViewModel;

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

        context = getContext();
        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.faqs_fragment, container, false);
        faqListViewModel = ViewModelProviders.of(this, viewModelFactory).get(FaqListViewModel.class);

        binding.createFaqFab.setOnClickListener(view -> {
            faqListViewModel.resetToDefaultState();
            openCreateFaqFragment();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        faqListViewModel.getProgress().observe(this, this::showProgress);
        faqListViewModel.getSuccess().observe(this, this::showMessage);
        faqListViewModel.getError().observe(this, this::showError);
        faqListViewModel.getFaqsLiveData().observe(this, this::showResults);
        faqListViewModel.getExitContextualMenuModeLiveData().observe(this, (exitContextualMenuMode) -> exitContextualMenuMode());
        faqListViewModel.getEnterContextualMenuModeLiveData().observe(this, (enterContextualMenuMode) -> enterContextualMenuMode());
        faqListViewModel.loadFaqs(false);
        faqListViewModel.listenChanges();
    }

    public void openCreateFaqFragment() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateFaqFragment.newInstance())
            .addToBackStack(null)
            .commit();
    }

    public ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_faqs, menu);
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
             return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.del:
                    showDeleteDialog();
                    break;
                default:
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode.finish();
            faqListViewModel.resetToDefaultState();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
        }
    };

    @Override
    protected int getTitle() {
        return R.string.faq;
    }

    @Override
    public void onStop() {
        super.onStop();
        faqListViewModel.getSelectedMap().clear();
        faqListViewModel.getFaqChangeListener().stopListening();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        faqsAdapter = new FaqListAdapter(faqListViewModel);

        RecyclerView recyclerView = binding.faqsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(faqsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            faqListViewModel.loadFaqs(true);
        });
    }

    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.question)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    faqListViewModel.deleteSelectedFaq();
                    faqListViewModel.resetToDefaultState();
                    exitContextualMenuMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void enterContextualMenuMode() {
        actionMode = getActivity().startActionMode(actionCallback);
    }

    @Override
    public void exitContextualMenuMode() {
        if (actionMode != null)
            actionMode.finish();
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
        faqListViewModel.resetToDefaultState();
        exitContextualMenuMode();
        if (success)
            ViewUtils.showSnackbar(binding.faqsRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.faqsRecyclerView, message);
    }

    @Override
    public void showResults(List<Faq> items) {
        if(items.isEmpty()) {
            showEmptyView(true);
            return;
        }
        faqsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

}
