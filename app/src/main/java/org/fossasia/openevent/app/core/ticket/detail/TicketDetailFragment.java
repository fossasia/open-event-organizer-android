package org.fossasia.openevent.app.core.ticket.detail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.TicketDetailLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class TicketDetailFragment extends BaseBottomSheetFragment<TicketDetailPresenter> implements TicketDetailView {

    private static final String TICKET_ID = "ticket_id";

    @Inject
    Lazy<TicketDetailPresenter> presenterProvider;

    private TicketDetailLayoutBinding binding;
    private long ticketId;
    private PrintManager printManager;

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
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(ticketId, this);
        getPresenter().start();
        binding.printAction.setOnClickListener(view -> {
            doPrint();
        });
    }

    private void doPrint() {
        Ticket ticket = getPresenter().getTicket();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name) +
                " Document";

            printManager.print(jobName, new TicketPrintAdapter(getActivity(), ticket),
                null);
        } else {
            ViewUtils.showSnackbar(binding.getRoot(), "No Printing Support!");
        }
    }

    @Override
    public Lazy<TicketDetailPresenter> getPresenterProvider() {
        return presenterProvider;
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
