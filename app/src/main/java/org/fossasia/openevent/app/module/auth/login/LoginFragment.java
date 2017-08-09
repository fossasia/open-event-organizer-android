package org.fossasia.openevent.app.module.auth.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.LoginFragmentBinding;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginView;
import org.fossasia.openevent.app.module.auth.signup.SignUpFragment;
import org.fossasia.openevent.app.module.main.MainActivity;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class LoginFragment extends BaseFragment<ILoginPresenter> implements ILoginView {

    @Inject
    Lazy<ILoginPresenter> presenterProvider;

    private LoginFragmentBinding binding;
    private Validator validator;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OrgaApplication
            .getAppComponent()
            .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setLogin(getPresenter().getLogin());
        getPresenter().start();

        binding.btnLogin.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            ViewUtils.hideKeyboard(getActivity());
            String url = binding.url.baseUrl.getText().toString().trim();
            getPresenter().setBaseUrl(url, binding.url.overrideUrl.isChecked());
            getPresenter().login();
        });

        binding.signUpLink.setOnClickListener(view -> openSignUpPage());
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

    private void openSignUpPage() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right)
            .replace(R.id.fragment_container, new SignUpFragment())
            .commit();
    }

    @Override
    public void showError(String error) {
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
    public Lazy<ILoginPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.login_fragment;
    }

    @Override
    public void attachEmails(Set<String> emails) {
        binding.emailDropdown.setAdapter(
            new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(emails))
        );
    }
}
