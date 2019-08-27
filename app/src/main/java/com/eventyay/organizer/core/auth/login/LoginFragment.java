package com.eventyay.organizer.core.auth.login;

import static com.eventyay.organizer.ui.ViewUtils.showView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import br.com.ilhasoft.support.validation.Validator;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.auth.SharedViewModel;
import com.eventyay.organizer.core.auth.reset.ResetPasswordFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.databinding.LoginFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import javax.inject.Inject;

public class LoginFragment extends BaseFragment implements LoginView {

    @Inject ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel loginFragmentViewModel;
    private LoginFragmentBinding binding;
    private Validator validator;
    private SharedViewModel sharedViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        loginFragmentViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        loginFragmentViewModel.getProgress().observe(this, this::showProgress);
        loginFragmentViewModel.getError().observe(this, this::showError);
        loginFragmentViewModel.getActionLogIn().observe(this, isLoggedIn -> handleIntent());
        loginFragmentViewModel
                .getLogin(sharedViewModel.getEmail().getValue())
                .observe(
                        this,
                        login -> {
                            binding.setLogin(login);
                            sharedViewModel
                                    .getEmail()
                                    .observe(this, email -> binding.getLogin().setEmail(email));
                        });
        loginFragmentViewModel
                .getActionOpenResetPassword()
                .observe(this, this::openResetPasswordFragment);
        loginFragmentViewModel.getBaseUrl().observe(this, this::setBaseUrl);

        loginFragmentViewModel.setBaseUrl();

        binding.btnLogin.setOnClickListener(
                view -> {
                    if (!validator.validate()) return;

                    ViewUtils.hideKeyboard(view);
                    loginFragmentViewModel.login();
                });
        binding.forgotPasswordLink.setOnClickListener(view -> clickForgotPassword());

        binding.emailLayout
                .getEditText()
                .addTextChangedListener(
                        new TextWatcher() {

                            @Override
                            public void beforeTextChanged(
                                    CharSequence s, int start, int count, int after) {
                                // do nothing
                            }

                            @Override
                            public void onTextChanged(
                                    CharSequence s, int start, int before, int count) {
                                if (start != 0) {
                                    sharedViewModel.setEmail(s.toString());
                                    getFragmentManager().popBackStack();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                // do nothing
                            }
                        });
    }

    public void handleIntent() {
        ViewUtils.hideKeyboard(binding.getRoot());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void setBaseUrl(String baseUrl) {
        binding.url.defaultUrl.setText(baseUrl);
    }

    @Override
    protected int getTitle() {
        return R.string.login;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.emailDropdown.setAdapter(null);
    }

    private void clickForgotPassword() {
        if (!validator.validate(binding.emailDropdown)) return;

        loginFragmentViewModel.requestToken();
        sharedViewModel.setEmail(binding.getLogin().getEmail());
    }

    @Override
    public void showError(String error) {
        ViewUtils.hideKeyboard(binding.getRoot());
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void openResetPasswordFragment(boolean resetPassword) {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_left,
                        R.anim.exit_from_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, new ResetPasswordFragment())
                .addToBackStack(null)
                .commit();
    }
}
