package org.fossasia.openevent.app.module.auth.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
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

public class LoginFragment extends BaseFragment<ILoginPresenter> implements ILoginView, AppCompatEditText.OnEditorActionListener {

    @Inject
    Lazy<ILoginPresenter> presenterProvider;

    @Inject
    HostSelectionInterceptor interceptor;

    private LoginFragmentBinding binding;
    private Validator validator;

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

        binding.btnLogin.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String email = binding.emailDropdown.getText().toString();
            String password = binding.etPassword.getText().toString();
            String url = binding.etBaseUrl.getText().toString().trim();

            getPresenter().setBaseUrl(interceptor, BuildConfig.DEFAULT_BASE_URL, url, checkBoxEnableUrl.isChecked());
            getPresenter().login(email, password);
        });

        binding.signUpLink.setOnClickListener(view -> openSignUpPage());
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.emailDropdown.setAdapter(null);
    }

    private void openSignUpPage() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, new SignUpFragment())
            .commit();
    }

    private void setEditTextListener() {
        binding.etBaseUrl.setOnEditorActionListener(this);
        binding.emailDropdown.setOnEditorActionListener(this);
        binding.etPassword.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            ViewUtils.hideKeyboard(getActivity());
            binding.btnLogin.performClick();
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
