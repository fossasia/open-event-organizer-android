package com.eventyay.organizer.core.auth.reset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.auth.SharedViewModel;
import com.eventyay.organizer.databinding.ResetPasswordByTokenFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class ResetPasswordFragment extends BaseFragment implements ResetPasswordView {

    private static final String TOKEN_KEY = "token";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ResetPasswordViewModel resetPasswordViewModel;
    private ResetPasswordByTokenFragmentBinding binding;
    private Validator validator;

    private String token;

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN_KEY, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            token = getArguments().getString(TOKEN_KEY);
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
        resetPasswordViewModel.getBaseUrl().observe(this, this::setBaseUrl);

        resetPasswordViewModel.setBaseUrl();

        if (token != null)
            resetPasswordViewModel.setToken(token);

        binding.btnResetPassword.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            if (!binding.newPassword.getText().toString()
                .equals(binding.confirmPassword.getText().toString())) {

                showError("Passwords Do not Match");
                return;
            }

            ViewUtils.hideKeyboard(view);
            resetPasswordViewModel.submitRequest(resetPasswordViewModel.getSubmitToken());
        });

        binding.loginLink.setOnClickListener(view -> getFragmentManager().popBackStack());

        binding.resendTokenLink.setOnClickListener(view -> resendToken());
    }

    private void resendToken() {
        SharedViewModel sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        resetPasswordViewModel.requestToken(sharedViewModel.getEmail().getValue());
    }

    private void setBaseUrl(String baseUrl) {
        binding.url.defaultUrl.setText(baseUrl);
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
