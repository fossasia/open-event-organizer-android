package org.fossasia.openevent.app.module.auth.signup;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.SignUpFragmentBinding;
import org.fossasia.openevent.app.module.auth.login.LoginFragment;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpPresenter;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpView;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class SignUpFragment extends BaseFragment<ISignUpPresenter> implements ISignUpView, AppCompatEditText.OnEditorActionListener {

    @Inject
    Lazy<ISignUpPresenter> presenterProvider;

    @Inject
    HostSelectionInterceptor interceptor;

    @Inject
    IUtilModel utilModel;

    private SignUpFragmentBinding binding;
    private Validator validator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OrgaApplication
            .getAppComponent()
            .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().start();

        setEditTextListener();

        CheckBox checkBoxEnableUrl = binding.checkboxEnableUrl;
        TextInputLayout addUrlContainer = binding.addUrlContainer;
        checkBoxEnableUrl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxEnableUrl.setTextColor(Color.BLACK);
                addUrlContainer.setVisibility(View.GONE);
            } else {
                checkBoxEnableUrl.setTextColor(Color.parseColor("#808080"));
                addUrlContainer.setVisibility(View.VISIBLE);
            }
        });

        binding.btnSignUp.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String firstName = binding.etFirstName.getText().toString();
            String lastName = binding.etLastName.getText().toString();
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            String url = binding.etBaseUrl.getText().toString().trim();

            getPresenter().setBaseUrl(interceptor, BuildConfig.DEFAULT_BASE_URL, url, checkBoxEnableUrl.isChecked());

            getPresenter().signUp(
                User.builder()
                    .email(email)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build());
        });
        binding.loginLink.setOnClickListener(view -> openLoginPage());
    }

    private void openLoginPage() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, new LoginFragment())
            .commit();
    }

    private void setEditTextListener() {
        binding.etBaseUrl.setOnEditorActionListener(this);
        binding.etEmail.setOnEditorActionListener(this);
        binding.etPassword.setOnEditorActionListener(this);
        binding.etFirstName.setOnEditorActionListener(this);
        binding.etLastName.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            ViewUtils.hideKeyboard(getActivity());
            binding.btnSignUp.performClick();
            return true;
        }
        return false;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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
    public Lazy<ISignUpPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.sign_up_fragment;
    }
}
