package com.eventyay.organizer.core.orders.list;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.orders.detail.OrderDetailFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.databinding.OrdersFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("PMD.TooManyMethods")
public class OrdersFragment extends BaseFragment implements OrdersView {

    private Context context;
    private long eventId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OrdersViewModel ordersViewModel;

    @Inject
    ContextUtils utilModel;

    private OrdersAdapter ordersAdapter;
    private OrdersFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static OrdersFragment newInstance(long eventId) {
        OrdersFragment fragment = new OrdersFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.orders_fragment, container, false);
        ordersViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrdersViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();

        ordersViewModel.getProgress().observe(this, this::showProgress);
        ordersViewModel.getError().observe(this, this::showError);
        ordersViewModel.getClickedOrder().observe(this, this::openOrderDetail);
        loadOrders(false);
    }

    @Override
    protected int getTitle() {
        return R.string.orders;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        ordersAdapter = new OrdersAdapter(ordersViewModel);

        RecyclerView recyclerView = binding.ordersRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(ordersAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            loadOrders(true);
        });
    }

    private void loadOrders(boolean reload) {
        ordersViewModel.getOrders(eventId, reload).observe(this, this::showResults);
    }

    private void openOrderDetail(Order order) {
        if (order == null)
            return;

        ordersViewModel.unselectListItem();

        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, OrderDetailFragment.newInstance(eventId, order.getIdentifier(), order.getId()))
            .addToBackStack(null)
            .commit();
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
    public void showResults(List<Order> orders) {
        if (orders.isEmpty()) {
            showEmptyView(true);
            return;
        }

        showEmptyView(false);
        ordersAdapter.setOrders(orders);
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(binding.ordersRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.ordersRecyclerView, message);
    }
}
