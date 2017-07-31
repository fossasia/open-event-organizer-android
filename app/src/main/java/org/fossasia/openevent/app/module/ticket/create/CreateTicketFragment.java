package org.fossasia.openevent.app.module.ticket.create;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.databinding.TicketCreateLayoutBinding;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketView;

import javax.inject.Inject;

import dagger.Lazy;

public class CreateTicketFragment extends BaseBottomSheetFragment<ICreateTicketPresenter> implements ICreateTicketView {

    @Inject
    Lazy<ICreateTicketPresenter> presenterProvider;

    private TicketCreateLayoutBinding binding;
    private TicketBinder ticketBinder;

    public static CreateTicketFragment newInstance() {
        return new CreateTicketFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        OrgaApplication.getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.ticket_create_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        ticketBinder = new TicketBinder(getPresenter().getTicket(), binding);

        binding.submit.setOnClickListener(view -> {
            if (ticketBinder.bound())
                getPresenter().createTicket();
        });
    }

    @Override
    public Lazy<ICreateTicketPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.ticket_create_layout;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
