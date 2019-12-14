package com.eventyay.organizer.core.role.invite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.databinding.FragmentRoleInviteBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.ui.ViewUtils.showView;
import static com.eventyay.organizer.utils.ValidateUtils.validateEmail;

public class RoleInviteFragment extends BaseFragment implements RoleInviteView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentRoleInviteBinding binding;

    private RoleInviteViewModel roleInviteViewModel;

    private long roleId;

    public static RoleInviteFragment newInstance(long eventId) {
        RoleInviteFragment fragment = new RoleInviteFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_role_invite, container, false);
        roleInviteViewModel = ViewModelProviders.of(this, viewModelFactory).get(RoleInviteViewModel.class);

        binding.btnSubmit.setOnClickListener(v -> {
            if (!validateEmail(binding.email.getText().toString())) {
                showError(getString(R.string.email_validation_error));
                return;
            }
            roleId = binding.selectRole.getSelectedItemPosition() + 1;
            roleInviteViewModel.createRoleInvite(roleId);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        roleInviteViewModel.getProgress().observe(this, this::showProgress);
        roleInviteViewModel.getSuccess().observe(this, this::onSuccess);
        roleInviteViewModel.getError().observe(this, this::showError);
        roleInviteViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        binding.setRoleInvite(roleInviteViewModel.getRoleInvite());
        setUpSpinner();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> rolesAdapter = ArrayAdapter
            .createFromResource(getActivity(), R.array.roles, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectRole.setAdapter(rolesAdapter);
    }

    @Override
    public int getTitle() {
        return R.string.add_role;
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
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
