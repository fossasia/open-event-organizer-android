package org.fossasia.openevent.app.core.auth.forgot.submit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.auth.forgot.request.ForgotPasswordFragment;
import org.fossasia.openevent.app.core.auth.login.LoginFragment;
import org.fossasia.openevent.app.databinding.ResetPasswordByTokenFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class ResetPasswordByTokenFragment extends BaseFragment<ResetPasswordByTokenPresenter> implements ResetPasswordByTokenView {

    @Inject
    Lazy<ResetPasswordByTokenPresenter> presenterProvider;

    private ResetPasswordByTokenFragmentBinding binding;
    private Validator validator;

    public static ResetPasswordByTokenFragment newInstance() {
        return new ResetPasswordByTokenFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.reset_password_by_token_fragment, container, false);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setSubmitToken(getPresenter().getSubmitToken());
        getPresenter().start();

        binding.btnResetPassword.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            if (!binding.newPassword.getText().toString()
                .equals(binding.confirmPassword.getText().toString())) {

                showError("Passwords Do not Match");
                return;
            }

            String url = binding.url.baseUrl.getText().toString().trim();
            getPresenter().setBaseUrl(url, binding.url.overrideUrl.isChecked());
            getPresenter().submitRequest();
        });

        binding.loginLink.setOnClickListener(view -> openLoginPage());

        binding.resendTokenLink.setOnClickListener(view -> openForgotPasswordPage());
    }

    @Override
    protected int getTitle() {
        return R.string.forgot_password_link;
    }

    private void openLoginPage() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, new LoginFragment())
            .commit();
    }

    private void openForgotPasswordPage() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, new ForgotPasswordFragment())
            .commit();
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
    public Lazy<ResetPasswordByTokenPresenter> getPresenterProvider() {
        return presenterProvider;
    }

}
