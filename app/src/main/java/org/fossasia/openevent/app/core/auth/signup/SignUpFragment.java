package org.fossasia.openevent.app.core.auth.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.auth.SharedViewModel;
import org.fossasia.openevent.app.core.auth.login.LoginFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.databinding.SignUpFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class SignUpFragment extends BaseFragment<SignUpPresenter> implements SignUpView {

    @Inject
    Lazy<SignUpPresenter> presenterProvider;

    @Inject
    ContextUtils utilModel;

    private SignUpFragmentBinding binding;
    private Validator validator;
    private SharedViewModel sharedViewModel;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        validator = new Validator(binding);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        sharedViewModel.getEmail().observe(this, email -> binding.getUser().setEmail(email));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setUser(getPresenter().getUser());

        validate(binding.emailLayout , ValidateUtils::validateEmail, getResources().getString(R.string.email_validation_error));

        binding.btnSignUp.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();
            if (!(getPresenter().arePasswordsEqual(password, confirmPassword))) {
                return;
            }

            String url = binding.url.baseUrl.getText().toString().trim();
            getPresenter().setBaseUrl(url, binding.url.overrideUrl.isChecked());
            getPresenter().signUp();
        });

        binding.privacyPolicy.setOnClickListener(view -> openPrivacyPolicy());
        binding.termsOfUse.setOnClickListener(view -> openTermsOfUse());
        binding.loginLink.setOnClickListener(view -> openLoginPage());
    }

    @Override
    public void validate(TextInputLayout textInputLayout, Function<String, Boolean> validationReference, String errorResponse) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validationReference.apply(charSequence.toString())) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(errorResponse);
                }
                if (TextUtils.isEmpty(charSequence)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing here
            }
        });
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.PRIVACY_POLICY_URL));
        startActivity(intent);
    }

    private void openTermsOfUse() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.TERMS_OF_USE_URL));
        startActivity(intent);
    }

    private void openLoginPage() {
        sharedViewModel.setEmail(binding.getUser().getEmail());
        getFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, new LoginFragment())
            .commit();
    }

    @Override
    public void showError(String error) {
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
    public Lazy<SignUpPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    protected int getTitle() {
        return R.string.sign_up;
    }
}
