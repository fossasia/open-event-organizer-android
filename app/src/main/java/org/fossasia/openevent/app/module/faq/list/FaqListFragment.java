package org.fossasia.openevent.app.module.faq.list;

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

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.FaqsFragmentBinding;
import org.fossasia.openevent.app.module.faq.create.CreateFaqFragment;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListPresenter;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListView;
import org.fossasia.openevent.app.module.main.MainActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class FaqListFragment extends BaseFragment<IFaqListPresenter> implements IFaqListView {

    private Context context;
    private long eventId;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<IFaqListPresenter> faqsPresenter;

    private FaqListAdapter faqsAdapter;
    private FaqsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private boolean initialized;

    public FaqListFragment() {
        OrgaApplication
            .getAppComponent()
            .inject(this);
    }

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
        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.faqs_fragment, container, false);

        binding.createFaqFab.setOnClickListener(view -> {
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
    public Lazy<IFaqListPresenter> getPresenterProvider() {
        return faqsPresenter;
    }

    @Override
    public int getLoaderId() {
        return R.layout.faqs_fragment;
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
            ViewUtils.showSnackbar(binding.faqsRecyclerView, R.string.refresh_complete);
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
