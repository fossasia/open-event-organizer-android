package com.eventyay.organizer.core.ticket.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.ticket.create.CreateTicketFragment;
import com.eventyay.organizer.core.ticket.detail.TicketDetailFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.TicketsFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class TicketsFragment extends BaseFragment<TicketsPresenter> implements TicketsView {

    private Context context;
    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<TicketsPresenter> ticketsPresenter;

    private TicketsAdapter ticketsAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private TicketsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static TicketsFragment newInstance(long eventId) {
        TicketsFragment fragment = new TicketsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.tickets_fragment, container, false);
        binding.createTicketFab.setOnClickListener(view -> {
            openCreateTicketFragment();
       });

        if (!getPresenter().getTickets().isEmpty()) {
            getPresenter().loadTickets(false);
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    protected int getTitle() {
        return R.string.tickets;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        ticketsAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    public void openCreateTicketFragment() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, CreateTicketFragment.newInstance())
            .addToBackStack(null)
            .commit();
    }

    private void setupRecyclerView() {
        ticketsAdapter = new TicketsAdapter(getPresenter());

        RecyclerView recyclerView = binding.ticketsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(ticketsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(ticketsAdapter);
        recyclerView.addItemDecoration(decoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        };

        ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.createTicketFab);

        ticketsAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadTickets(true);
        });
    }

    @Override
    public Lazy<TicketsPresenter> getPresenterProvider() {
        return ticketsPresenter;
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
            ViewUtils.showSnackbar(binding.ticketsRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Ticket> items) {
        ticketsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void showTicketDeleted(String message) {
        ViewUtils.showSnackbar(binding.ticketsRecyclerView, message);
    }

    @Override
    public void openTicketDetailFragment(long ticketId) {
        BottomSheetDialogFragment bottomSheetDialogFragment = TicketDetailFragment.newInstance(ticketId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}
