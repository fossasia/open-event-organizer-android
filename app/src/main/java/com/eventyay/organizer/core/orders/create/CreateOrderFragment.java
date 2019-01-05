package com.eventyay.organizer.core.orders.create;

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

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.orders.create.adapter.CreateOrderTicketsAdapter;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.OrderCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class CreateOrderFragment extends BaseFragment implements CreateOrderView {

    private long eventId;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateOrderViewModel createOrderViewModel;
    private CreateOrderTicketsAdapter createOrderTicketsAdapter;

    private OrderCreateLayoutBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static CreateOrderFragment newInstance(long eventId) {
        CreateOrderFragment fragment = new CreateOrderFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.order_create_layout, container, false);

        createOrderViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateOrderViewModel.class);
        binding.clearButton.setOnClickListener(view -> createOrderViewModel.clearSelectedTickets());
        binding.submit.setOnClickListener(view -> createOrderViewModel.createOnSiteOrder(eventId));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        setupRecyclerView();

        createOrderViewModel.getProgress().observe(this, this::showProgress);
        createOrderViewModel.getError().observe(this, this::showError);
        createOrderViewModel.getSuccess().observe(this, this::onSuccess);
        createOrderViewModel.getOrderAmount().observe(this, this::showOrderAmount);

        loadData(false);
    }

    @Override
    protected int getTitle() {
        return R.string.create_order;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        createOrderTicketsAdapter = new CreateOrderTicketsAdapter(createOrderViewModel);

        RecyclerView ticketsRecyclerView = binding.ticketsRecyclerView;
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ticketsRecyclerView.setAdapter(createOrderTicketsAdapter);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            loadData(true);
        });
    }

    public void showOrderAmount(Float amount) {
        binding.orderAmount.setText(String.valueOf(amount));
        if(amount == 0.0) {
            binding.clearButton.setVisibility(View.GONE);
        } else {
            binding.clearButton.setVisibility(View.VISIBLE);
        }
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
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    public void loadData(boolean reload) {
        createOrderViewModel.getTicketsUnderEvent(eventId, reload).observe(this, this::showTickets);
    }

    public void showTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            binding.ticketsInfo.setVisibility(View.GONE);
            binding.submit.setVisibility(View.GONE);
            return;
        }
        createOrderTicketsAdapter.setTickets(tickets);
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
