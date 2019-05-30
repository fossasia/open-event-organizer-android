package com.eventyay.organizer.core.roleinvites;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.databinding.FragmentRoleInvitesBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;


import static com.eventyay.organizer.ui.ViewUtils.showView;
import static com.eventyay.organizer.utils.ValidateUtils.validateEmail;

public class RoleInviteFragment extends BaseFragment implements RoleInviteView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentRoleInvitesBinding binding;
    private ArrayAdapter<CharSequence> rolesAdapter;

    private RoleInviteViewModel roleInviteViewModel;

    private long eventId;
    private long roleId;

    public static RoleInviteFragment newInstance(long eventId) {
        RoleInviteFragment fragment = new RoleInviteFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_role_invites, container, false);
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
        roleInviteViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        roleInviteViewModel.getSuccess().observe(this, this::onSuccess);
        roleInviteViewModel.getError().observe(this, this::showError);
        binding.setRoleInvite(roleInviteViewModel.getRoleInvite());
        setUpSpinner();
    }

    private void setUpSpinner() {
        rolesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.roles, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectRole.setAdapter(rolesAdapter);
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

    @Override
    public int getTitle() {
        return R.string.add_role;
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
