package org.fossasia.openevent.app.event.checkin;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.BR;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.databinding.BottomsheetAttendeeCheckInBinding;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInView;
import org.fossasia.openevent.app.utils.ViewUtils;

import javax.inject.Inject;

public class AttendeeCheckInFragment extends BottomSheetDialogFragment implements IAttendeeCheckInView {

    private static final String ATTENDEE_ID = "attendee_id";

    private BottomsheetAttendeeCheckInBinding binding;

    @Inject
    IAttendeeCheckInPresenter presenter;

    public static AttendeeCheckInFragment newInstance(long attendeeId) {
        Bundle args = new Bundle();
        args.putLong(ATTENDEE_ID, attendeeId);

        AttendeeCheckInFragment fragment = new AttendeeCheckInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(getContext())
            .inject(this);

        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            long attendeeId = args.getLong(ATTENDEE_ID);
            presenter.attach(attendeeId, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.bottomsheet_attendee_check_in, container, false);
        binding.setPresenter(presenter);
        presenter.start();

        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detach();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAttendee(Attendee attendee) {
        binding.setCheckinAttendee(attendee);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, View.INVISIBLE, show);
    }

    @Override
    public void onSuccess(String message) {
        binding.notifyPropertyChanged(BR.checkinAttendee);
        showToast(message);
    }

    @Override
    public void onError(String message) {
        showToast(message);
    }
}
