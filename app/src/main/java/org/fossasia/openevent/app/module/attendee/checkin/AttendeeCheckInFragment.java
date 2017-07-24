package org.fossasia.openevent.app.module.attendee.checkin;

import android.content.DialogInterface;
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
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.databinding.BottomsheetAttendeeCheckInBinding;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInView;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;

import javax.inject.Inject;

import io.reactivex.functions.Action;

public class AttendeeCheckInFragment extends BottomSheetDialogFragment implements IAttendeeCheckInView {

    private static final String ATTENDEE_ID = "attendee_id";

    private BottomsheetAttendeeCheckInBinding binding;
    private Action onCancel;

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

    @Override
    public void onCancel(DialogInterface dialog) throws IllegalStateException {
        super.onCancel(dialog);
        try {
            if (onCancel != null)
                onCancel.run();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    public void setOnCancelListener(Action onCancel) {
        this.onCancel = onCancel;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(Attendee attendee) {
        binding.setCheckinAttendee(attendee);
        binding.executePendingBindings();
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
    public void showError(String message) {
        showToast(message);
    }


}
