package com.eventyay.organizer.core.event.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.databinding.EventDetailsStepTwoBinding;
import com.eventyay.organizer.utils.ValidateUtils;
import javax.inject.Inject;

public class EventDetailsStepTwo extends BaseBottomSheetFragment
        implements EventDetailsStepTwoView {

    @Inject ViewModelProvider.Factory viewModelFactory;

    private EventDetailsStepTwoBinding binding;

    private CreateEventViewModel createEventViewModel;

    public static EventDetailsStepTwo newInstance() {
        return new EventDetailsStepTwo();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding =
                DataBindingUtil.inflate(
                        inflater, R.layout.event_details_step_two, container, false);
        createEventViewModel =
                ViewModelProviders.of(getActivity(), viewModelFactory)
                        .get(CreateEventViewModel.class);
        createEventViewModel.getEvent().isSponsorsEnabled = true;
        createEventViewModel.getEvent().isSessionsSpeakersEnabled = true;
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setEvent(createEventViewModel.getEvent());
        ValidateUtils.validate(
                binding.ticketUrlLayout,
                ValidateUtils::validateUrl,
                getResources().getString(R.string.url_validation_error));
    }
}
