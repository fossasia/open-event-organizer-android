package com.eventyay.organizer.core.auth.reset;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.auth.SharedViewModel;
import com.eventyay.organizer.databinding.ResetPasswordByTokenFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;
import static com.eventyay.organizer.utils.ValidateUtils.validate;
import static com.eventyay.organizer.utils.ValidateUtils.validateUrl;

public class ResetPasswordFragment extends BaseFragment implements ResetPasswordView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ResetPasswordViewModel resetPasswordViewModel;
    private ResetPasswordByTokenFragmentBinding binding;
    private Validator validator;

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.reset_password_by_token_fragment, container, false);
        resetPasswordViewModel = ViewModelProviders.of(this, viewModelFactory).get(ResetPasswordViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.setSubmitToken(resetPasswordViewModel.getSubmitToken());

        resetPasswordViewModel.getProgress().observe(this, this::showProgress);
        resetPasswordViewModel.getError().observe(this, this::showError);
        resetPasswordViewModel.getSuccess().observe(this, this::onSuccess);
        resetPasswordViewModel.getMessage().observe(this, this::showMessage);

        validate(binding.url.baseUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));

        binding.url.toggleUrl.setOnClickListener(view -> {

            if (binding.url.baseUrlLayout.getVisibility() == View.VISIBLE) {
                binding.url.toggleUrl.setText(getString(R.string.use_another_url));
                binding.url.baseUrlLayout.setVisibility(View.GONE);
            } else {
                binding.url.toggleUrl.setText(getString(R.string.use_default_url));
                binding.url.baseUrlLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.btnResetPassword.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String url = binding.url.baseUrl.getText().toString().trim();

            boolean isBaseUrlLayoutVisible = binding.url.baseUrlLayout.getVisibility() == View.VISIBLE;

            if(isBaseUrlLayoutVisible && !validateUrl(url)) {
                return;
            }

            if (!binding.newPassword.getText().toString()
                .equals(binding.confirmPassword.getText().toString())) {

                showError("Passwords Do not Match");
                return;
            }

            ViewUtils.hideKeyboard(view);
            resetPasswordViewModel.setBaseUrl(url, !isBaseUrlLayoutVisible);
            resetPasswordViewModel.submitRequest(resetPasswordViewModel.getSubmitToken());
        });

        binding.loginLink.setOnClickListener(view -> getFragmentManager().popBackStack());

        binding.resendTokenLink.setOnClickListener(view -> resendToken());
    }

    private void resendToken() {
        SharedViewModel sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        resetPasswordViewModel.requestToken(sharedViewModel.getEmail().getValue());
    }

    @Override
    protected int getTitle() {
        return R.string.reset_password;
    }

    @Override
    public void showError(String error) {
        ViewUtils.hideKeyboard(binding.getRoot());
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
