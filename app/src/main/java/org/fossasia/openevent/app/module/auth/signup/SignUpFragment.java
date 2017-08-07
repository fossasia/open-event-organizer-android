package org.fossasia.openevent.app.module.auth.signup;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.databinding.SignUpFragmentBinding;
import org.fossasia.openevent.app.module.auth.login.LoginFragment;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpPresenter;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpView;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class SignUpFragment extends BaseFragment<ISignUpPresenter> implements ISignUpView {

    @Inject
    Lazy<ISignUpPresenter> presenterProvider;

    @Inject
    IUtilModel utilModel;

    private SignUpFragmentBinding binding;
    private Validator validator;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

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
        binding.setUser(getPresenter().getUser());

        binding.btnSignUp.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String url = binding.url.baseUrl.getText().toString().trim();
            getPresenter().setBaseUrl(url, binding.url.overrideUrl.isChecked());
            getPresenter().signUp();
        });
        binding.loginLink.setOnClickListener(view -> openLoginPage());
    }

    private void openLoginPage() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, new LoginFragment())
            .commit();
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

    @Override
    protected int getTitle() {
        return R.string.sign_up;
    }
}
