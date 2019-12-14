package com.eventyay.organizer.core.settings.restriction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.TicketSettingsFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

public class CheckInRestrictions extends BaseFragment implements CheckInRestrictionView {

    private Context context;
    private long eventId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TicketSettingsViewModel ticketSettingsViewModel;

    private CheckInRestrictionsAdapter ticketsAdapter;
    private TicketSettingsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static CheckInRestrictions newInstance(long eventId) {
        CheckInRestrictions fragment = new CheckInRestrictions();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.ticket_settings_fragment, container, false);

        ticketSettingsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(TicketSettingsViewModel.class);

        ticketSettingsViewModel.getProgress().observe(this, this::showProgress);
        ticketSettingsViewModel.getError().observe(this, this::showError);
        ticketSettingsViewModel.getTickets().observe(this, (newTickets) -> {
            ticketsAdapter.setTickets(newTickets);
            checkRestrictAll();
        });
        ticketSettingsViewModel.getTicketUpdatedAction().observe(this, (aVoid) -> {
            checkRestrictAll();
        });

        ticketSettingsViewModel.loadTickets(eventId);

        binding.restrictAll.setOnClickListener(v -> {
            restrictAll(!binding.restrictAllCheckbox.isChecked());
        });
        binding.restrictAllCheckbox.setOnClickListener(v -> {
            //checkbox already checked
            restrictAll(binding.restrictAllCheckbox.isChecked());
        });

        return binding.getRoot();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private void checkRestrictAll() {
        if (ticketSettingsViewModel.getTickets() == null) {
            return;
        }

        boolean restrictAll = true;

        for (Ticket ticket : ticketSettingsViewModel.getTickets().getValue()) {
            if (!ticket.isCheckinRestricted) {
                restrictAll = false;
                break;
            }
        }

        binding.restrictAllCheckbox.setChecked(restrictAll);
    }

    private void restrictAll(boolean toRestrict) {
        binding.restrictAllCheckbox.setChecked(toRestrict);
        ticketSettingsViewModel.updateAllTickets(toRestrict);
        ticketsAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getTitle() {
        return R.string.check_in_restrictions;
    }

    private void setupRecyclerView() {
        ticketsAdapter = new CheckInRestrictionsAdapter(ticketSettingsViewModel.getTickets().getValue(), ticketSettingsViewModel::updateTicket);

        RecyclerView recyclerView = binding.ticketsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(ticketsAdapter);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            ticketSettingsViewModel.loadTickets(eventId);
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
}
