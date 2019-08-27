package com.eventyay.organizer.core.settings.autocheckin;

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
import com.eventyay.organizer.databinding.AutoCheckInFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import javax.inject.Inject;

public class AutoCheckInFragment extends BaseFragment implements AutoCheckInView {

    private Context context;
    private long eventId;

    @Inject ViewModelProvider.Factory viewModelFactory;

    private AutoCheckInViewModel autoCheckInViewModel;

    private AutoCheckInAdapter ticketsAdapter;
    private AutoCheckInFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static AutoCheckInFragment newInstance(long eventId) {
        AutoCheckInFragment fragment = new AutoCheckInFragment();
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
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding =
                DataBindingUtil.inflate(
                        inflater, R.layout.auto_check_in_fragment, container, false);

        autoCheckInViewModel =
                ViewModelProviders.of(getActivity(), viewModelFactory)
                        .get(AutoCheckInViewModel.class);

        autoCheckInViewModel.getProgress().observe(this, this::showProgress);
        autoCheckInViewModel.getError().observe(this, this::showError);
        autoCheckInViewModel
                .getTickets()
                .observe(
                        this,
                        (newTickets) -> {
                            ticketsAdapter.setTickets(newTickets);
                            autoCheckInAll();
                        });
        autoCheckInViewModel
                .getTicketUpdatedAction()
                .observe(
                        this,
                        (aVoid) -> {
                            autoCheckInAll();
                        });

        autoCheckInViewModel.loadTickets(eventId);

        binding.autoCheckInAll.setOnClickListener(
                v -> {
                    autoCheckInAll(!binding.autoCheckInAllCheckbox.isChecked());
                });
        binding.autoCheckInAllCheckbox.setOnClickListener(
                v -> {
                    // checkbox already checked
                    autoCheckInAll(binding.autoCheckInAllCheckbox.isChecked());
                });

        return binding.getRoot();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private void autoCheckInAll() {
        if (autoCheckInViewModel.getTickets() == null) {
            return;
        }

        boolean autoCheckInAll = true;

        for (Ticket ticket : autoCheckInViewModel.getTickets().getValue()) {
            if (!ticket.autoCheckinEnabled) {
                autoCheckInAll = false;
                break;
            }
        }

        binding.autoCheckInAllCheckbox.setChecked(autoCheckInAll);
    }

    private void autoCheckInAll(boolean toAutoCheckIn) {
        binding.autoCheckInAllCheckbox.setChecked(toAutoCheckIn);
        autoCheckInViewModel.updateAllTickets(toAutoCheckIn);
        ticketsAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getTitle() {
        return R.string.auto_check_in;
    }

    private void setupRecyclerView() {
        ticketsAdapter =
                new AutoCheckInAdapter(
                        autoCheckInViewModel.getTickets().getValue(),
                        autoCheckInViewModel::updateTicket);

        RecyclerView recyclerView = binding.ticketsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(ticketsAdapter);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(
                () -> {
                    refreshLayout.setRefreshing(false);
                    autoCheckInViewModel.loadTickets(eventId);
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
