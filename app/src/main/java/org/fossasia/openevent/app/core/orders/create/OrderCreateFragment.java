package org.fossasia.openevent.app.core.orders.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.orders.create.adapter.EventTicketsAdapter;
import org.fossasia.openevent.app.core.orders.detail.OrderDetailView;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.OrderDetailFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class OrderCreateFragment extends BaseFragment implements OrderDetailView {

    private long eventId;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OrderCreateViewModel orderCreateViewModel;
    private EventTicketsAdapter eventTicketsAdapter;

    private OrderDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static OrderCreateFragment newInstance(long eventId) {
        OrderCreateFragment fragment = new OrderCreateFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        context = getContext();

        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.order_detail_fragment, container, false);

        orderCreateViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderCreateViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        setupRecyclerView();

        orderCreateViewModel.getProgress().observe(this, this::showProgress);
        orderCreateViewModel.getError().observe(this, this::showError);
        loadData(false);
    }

    @Override
    protected int getTitle() {
        return R.string.order_details;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        eventTicketsAdapter = new EventTicketsAdapter();

        RecyclerView ticketsRecyclerView = binding.ticketsRecyclerView;
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ticketsRecyclerView.setAdapter(eventTicketsAdapter);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            loadData(true);
        });
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    public void loadData(boolean reload) {
        orderCreateViewModel.getTicketsUnderOrder(eventId, reload).observe(this, this::showTickets);
    }

    public void showTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            binding.ticketsInfo.setVisibility(View.GONE);
            return;
        }
        eventTicketsAdapter.setTickets(tickets);
    }

    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
    }
}
