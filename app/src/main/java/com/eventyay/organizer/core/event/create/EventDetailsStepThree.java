package com.eventyay.organizer.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.databinding.EventDetailsStepThreeBinding;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class EventDetailsStepThree extends BaseBottomSheetFragment implements EventDetailsStepThreeView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EventDetailsStepThreeBinding binding;
    private CreateEventViewModel createEventViewModel;
    private Validator validator;

    public static Fragment newInstance() {
        return new EventDetailsStepThree();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_step_three, container, false);
        createEventViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CreateEventViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setEvent(createEventViewModel.getEvent());
        createEventViewModel.getSuccessMessage().observe(this, this::onSuccess);
        createEventViewModel.getErrorMessage().observe(this, this::showError);
        createEventViewModel.getCloseState().observe(this, isClosed -> close());
        createEventViewModel.getProgress().observe(this, this::showProgress);
        createEventViewModel.getEvent().isTaxEnabled = true;

        ValidateUtils.validate(binding.logoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        ValidateUtils.validate(binding.externalEventUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        ValidateUtils.validate(binding.originalImageUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));

        getActivity().findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if (validator.validate()) {
                createEventViewModel.createEvent();
            }
        });
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void close() {
        getActivity().finish();
    }

}
