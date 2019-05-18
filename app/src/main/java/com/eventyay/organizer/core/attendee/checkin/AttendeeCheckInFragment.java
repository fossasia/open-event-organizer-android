package com.eventyay.organizer.core.attendee.checkin;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.core.attendee.history.CheckInHistoryFragment;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.BottomsheetAttendeeCheckInBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;


public class AttendeeCheckInFragment extends BaseBottomSheetFragment implements AttendeeCheckInView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private static final String ATTENDEE_ID = "attendee_id";

    private BottomsheetAttendeeCheckInBinding binding;
    private Runnable onCancelAction;
    private long attendeeId;
    private AttendeeCheckInViewModel attendeeCheckInViewModel;

    public static AttendeeCheckInFragment newInstance(long attendeeId) {
        Bundle args = new Bundle();
        args.putLong(ATTENDEE_ID, attendeeId);
        AttendeeCheckInFragment fragment = new AttendeeCheckInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null)
            attendeeId = args.getLong(ATTENDEE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_attendee_check_in, container, false);
        attendeeCheckInViewModel = ViewModelProviders.of(this, viewModelFactory).get(AttendeeCheckInViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setViewModel(attendeeCheckInViewModel);
        binding.switchAttendeeDetailsState.setChecked(false);
        attendeeCheckInViewModel.start(attendeeId);
        binding.activityLog.setOnClickListener(view -> openCheckInHistoryFragment());
        attendeeCheckInViewModel.getAttendee().observe(this, this::showResult);
    }

    private void openCheckInHistoryFragment() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, CheckInHistoryFragment.newInstance(attendeeId))
            .addToBackStack(null)
            .commit();

        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelAction != null)
            onCancelAction.run();
    }

    public void setOnCancelListener(Runnable onCancel) {
        this.onCancelAction = onCancel;
    }

    @Override
    public void showResult(Attendee attendee) {
        binding.setCheckinAttendee(attendee);
        binding.executePendingBindings();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

}
