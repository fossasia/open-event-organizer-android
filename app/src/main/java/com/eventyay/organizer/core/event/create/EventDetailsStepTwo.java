package com.eventyay.organizer.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.databinding.EventDetailsStepTwoBinding;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;

public class EventDetailsStepTwo extends BaseBottomSheetFragment implements EventDetailsStepTwoView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EventDetailsStepTwoBinding binding;

    private CreateEventViewModel createEventViewModel;

    public static EventDetailsStepTwo newInstance() {
        return new EventDetailsStepTwo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_step_two, container, false);
        createEventViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CreateEventViewModel.class);
        createEventViewModel.getEvent().isSponsorsEnabled = true;
        createEventViewModel.getEvent().isSessionsSpeakersEnabled = true;
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setEvent(createEventViewModel.getEvent());
        validate(binding.ticketUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
    }

    @Override
    public void validate(TextInputLayout textInputLayout, Function<String, Boolean> validationReference, String errorResponse) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validationReference.apply(charSequence.toString())) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(errorResponse);
                }
                if (TextUtils.isEmpty(charSequence)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing here
            }
        });
    }

}
