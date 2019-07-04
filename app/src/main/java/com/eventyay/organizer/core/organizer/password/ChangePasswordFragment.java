package com.eventyay.organizer.core.organizer.password;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.ChangePasswordFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class ChangePasswordFragment extends BaseFragment implements ChangePasswordView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ChangePasswordFragmentBinding binding;
    private Validator validator;
    private ChangePasswordViewModel changePasswordViewModel;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.change_password_fragment, container, false);
        validator = new Validator(binding);
        changePasswordViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChangePasswordViewModel.class);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setOrganizerPassword(changePasswordViewModel.getChangePasswordObject());
        changePasswordViewModel.getProgress().observe(this, this::showProgress);
        changePasswordViewModel.getSuccess().observe(this, this::onSuccess);
        changePasswordViewModel.getError().observe(this, this::showError);
        changePasswordViewModel.getBaseUrl().observe(this, this::setBaseUrl);

        changePasswordViewModel.setBaseUrl();

        binding.btnChangePassword.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            ViewUtils.hideKeyboard(view);
            changePasswordViewModel.changePasswordRequest(binding.oldPassword.getText().toString(),
                binding.newPassword.getText().toString(),
                binding.confirmNewPassword.getText().toString());

        });
    }

    private void setBaseUrl(String baseUrl) {
        binding.url.defaultUrl.setText(baseUrl);
    }

    @Override
    protected int getTitle() {
        return R.string.change_password;
    }

    @Override
    public void showError(String error) {
        ViewUtils.hideKeyboard(binding.getRoot());
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

}
