package com.eventyay.organizer.core.ticket.detail;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.TicketDetailLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

public class TicketDetailFragment extends BaseBottomSheetFragment implements TicketDetailView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private static final String TICKET_ID = "ticket_id";
    private TicketDetailViewModel ticketDetailViewModel;

    private TicketDetailLayoutBinding binding;
    private long ticketId;

    public static TicketDetailFragment newInstance(long ticketId) {
        Bundle args = new Bundle();
        
        args.putLong(TICKET_ID, ticketId);

        TicketDetailFragment fragment = new TicketDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null)
            ticketId = args.getLong(TICKET_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ticket_detail_layout, container, false);
        ticketDetailViewModel =  ViewModelProviders.of(this, viewModelFactory).get(TicketDetailViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ticketDetailViewModel.loadTicket(ticketId);
        ticketDetailViewModel.getError().observe(this, this::showError);
        ticketDetailViewModel.getTicket().observe(this, this::showResult);
        binding.printAction.setOnClickListener(view -> {
            doPrint();
        });
    }

    private void doPrint() {
        PrintManager printManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name) + " Document";
            Ticket ticket = ticketDetailViewModel.getTicket().getValue();
            printManager.print(jobName, new TicketPrintAdapter(getActivity(), ticket), null);
        } else {
            ViewUtils.showSnackbar(binding.getRoot(), "No Printing Support!");
        }
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showResult(Ticket item) {
        binding.setTicket(item);
    }
}
