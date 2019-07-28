package com.eventyay.organizer.core.orders.detail;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.orders.detail.adapter.OrderAttendeesAdapter;
import com.eventyay.organizer.core.orders.detail.adapter.OrderDetailsPrintAdapter;
import com.eventyay.organizer.core.orders.detail.adapter.OrderTicketsAdapter;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.OrderDetailFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class OrderDetailFragment extends BaseFragment implements OrderDetailView {

    private static final String ORDER_IDENTIFIER_KEY = "order_identifier";
    private static final String ORDER_ID_KEY = "order_id";
    private String orderIdentifier;
    private long eventId;
    private Order order;
    private long orderId;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OrderDetailViewModel orderDetailViewModel;
    private OrderAttendeesAdapter orderAttendeesAdapter;
    private OrderTicketsAdapter orderTicketsAdapter;

    private OrderDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static OrderDetailFragment newInstance(long eventId, String orderIdentifier, Long orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        args.putString(ORDER_IDENTIFIER_KEY, orderIdentifier);
        args.putLong(ORDER_ID_KEY, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        context = getContext();

        if (getArguments() != null) {
            orderIdentifier = getArguments().getString(ORDER_IDENTIFIER_KEY);
            orderId = getArguments().getLong(ORDER_ID_KEY);
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
        setupRecyclerView();

        orderDetailViewModel.getOrder(orderIdentifier, eventId, false).observe(this, this::showOrderDetails);
        orderDetailViewModel.getProgress().observe(this, this::showProgress);
        orderDetailViewModel.getSuccess().observe(this, this::onSuccess);
        orderDetailViewModel.getError().observe(this, this::showError);
        loadData(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            binding.printAction.setVisibility(View.GONE);

       binding.printAction.setOnClickListener(view -> {
            doPrint();
        });

        binding.emailReceipt.setOnClickListener(view -> {
            sendReceipt();
        });
    }

    private void doPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name) + " Document";
            printManager.print(jobName, new OrderDetailsPrintAdapter(getActivity(), order), null);
        }
    }

    private void sendReceipt() {
        orderDetailViewModel.sendReceipt(orderIdentifier);
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
        orderAttendeesAdapter = new OrderAttendeesAdapter();
        orderTicketsAdapter = new OrderTicketsAdapter();

        RecyclerView attendeesRecyclerView = binding.attendeesRecyclerView;
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        attendeesRecyclerView.setAdapter(orderAttendeesAdapter);

        RecyclerView ticketsRecyclerView = binding.ticketsRecyclerView;
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ticketsRecyclerView.setAdapter(orderTicketsAdapter);

        SwipeController swipeController = new SwipeController(orderDetailViewModel, orderAttendeesAdapter, context);

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(attendeesRecyclerView);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            orderDetailViewModel.getOrder(orderIdentifier, eventId, true).observe(this, this::showOrderDetails);
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
        orderDetailViewModel.getAttendeesUnderOrder(orderIdentifier, orderId, reload).observe(this, this::showAttendees);
        orderDetailViewModel.getTicketsUnderOrder(orderIdentifier, orderId, reload).observe(this, this::showTickets);
    }

    public void showAttendees(List<Attendee> attendees) {
        orderAttendeesAdapter.setAttendees(attendees);
    }

    public void showTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            binding.ticketsInfo.setVisibility(View.GONE);
            return;
        }
        orderTicketsAdapter.setTickets(tickets);
    }

    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        // Nothing to do
    }

    public void showOrderDetails(Order order) {
        binding.setOrder(order);
        this.order = order;

        if (order == null) {
            showEmptyView(true);
        }

        if (TextUtils.equals("completed", order.status)) {
            binding.emailReceiptLl.setVisibility(View.VISIBLE);
        } else {
            binding.emailReceiptLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
