package org.fossasia.openevent.app.core.auth.reset;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.auth.SharedViewModel;
import org.fossasia.openevent.app.databinding.ResetPasswordByTokenFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import androidx.navigation.fragment.NavHostFragment;
import br.com.ilhasoft.support.validation.Validator;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class ResetPasswordFragment extends BaseFragment implements ResetPasswordView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ResetPasswordViewModel resetPasswordViewModel;
    private ResetPasswordByTokenFragmentBinding binding;
    private Validator validator;

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

        binding.btnResetPassword.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            if (!binding.newPassword.getText().toString()
                .equals(binding.confirmPassword.getText().toString())) {

                showError("Passwords Do not Match");
                return;
            }

            String url = binding.url.baseUrl.getText().toString().trim();
            resetPasswordViewModel.setBaseUrl(url, binding.url.overrideUrl.isChecked());
            resetPasswordViewModel.submitRequest(resetPasswordViewModel.getSubmitToken());
        });

        binding.loginLink.setOnClickListener(view -> openLoginPage());

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

    private void openLoginPage() {
        NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
    }

    @Override
    public void showError(String error) {
        ViewUtils.hideKeyboard(binding.getRoot());
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        openLoginPage();
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
