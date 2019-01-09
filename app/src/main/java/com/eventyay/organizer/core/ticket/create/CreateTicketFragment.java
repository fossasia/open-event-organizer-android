package com.eventyay.organizer.core.ticket.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.TicketCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class CreateTicketFragment extends BaseFragment implements CreateTicketView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateTicketViewModel createTicketViewModel;
    private TicketCreateLayoutBinding binding;
    private Validator validator;

    public static CreateTicketFragment newInstance() {
        return new CreateTicketFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.ticket_create_layout, container, false);
        binding.form.name.requestFocus();
        ViewUtils.showKeyboard(getContext());
        createTicketViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateTicketViewModel.class);
        validator = new Validator(binding.form);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                createTicketViewModel.createTicket();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createTicketViewModel.getProgress().observe(this, this::showProgress);
        createTicketViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        createTicketViewModel.getSuccess().observe(this, this::onSuccess);
        createTicketViewModel.getError().observe(this, this::showError);
        binding.setTicket(createTicketViewModel.getTicket());
    }

    @Override
    protected int getTitle() {
        return R.string.create_ticket;
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
