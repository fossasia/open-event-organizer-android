package org.fossasia.openevent.app.core.attendee.checkin;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.core.attendee.history.CheckInHistoryFragment;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.databinding.BottomsheetAttendeeCheckInBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

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

        binding.activityLog.setOnClickListener(view -> openCheckInHistoryFragment());
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
