package org.fossasia.openevent.app.core.orders.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.databinding.OrderDetailFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

public class OrderDetailFragment extends BaseFragment implements OrderDetailView {

    private static final String ORDER_KEY = "order_id";
    private String orderIdentifier;
    private long eventId;
    private Order order;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OrderDetailViewModel orderDetailViewModel;

    private OrderDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static OrderDetailFragment newInstance(long eventId, String orderIdentifier) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        args.putString(ORDER_KEY, orderIdentifier);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            orderIdentifier = getArguments().getString(ORDER_KEY);
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.order_detail_fragment, container, false);

        orderDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderDetailViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();

        orderDetailViewModel.getOrder(orderIdentifier, eventId, false).observe(this, this::showOrderDetails);
        orderDetailViewModel.getProgress().observe(this, this::showProgress);
        orderDetailViewModel.getError().observe(this, this::showError);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            binding.printAction.setVisibility(View.GONE);

        binding.printAction.setOnClickListener(view -> {
            doPrint();
        });
    }

    private void doPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name) + " Document";
            printManager.print(jobName, new OrderDetailsPrintAdapter(getActivity(), order), null);
        }
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

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            orderDetailViewModel.getOrder(orderIdentifier, eventId, true).observe(this, this::showOrderDetails);
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

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
    }

    public void showOrderDetails(Order order) {
        binding.setOrder(order);
        this.order = order;
    }
}
