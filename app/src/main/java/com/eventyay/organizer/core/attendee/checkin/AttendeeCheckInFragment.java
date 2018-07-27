package com.eventyay.organizer.core.attendee.checkin;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.BottomsheetAttendeeCheckInBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class AttendeeCheckInFragment extends BaseBottomSheetFragment<AttendeeCheckInPresenter> implements AttendeeCheckInView {

    private static final String ATTENDEE_ID = "attendee_id";

    private BottomsheetAttendeeCheckInBinding binding;
    private Runnable onCancelAction;
    private long attendeeId;

    @Inject
    Lazy<AttendeeCheckInPresenter> presenterProvider;

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
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(attendeeId, this);
        binding.setPresenter(getPresenter());
        binding.switchAttendeeDetailsState.setChecked(false);
        getPresenter().start();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelAction != null)
            onCancelAction.run();
    }

    @Override
    public Lazy<AttendeeCheckInPresenter> getPresenterProvider() {
        return presenterProvider;
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
