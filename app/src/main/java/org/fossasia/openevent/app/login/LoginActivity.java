package org.fossasia.openevent.app.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseActivity;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.databinding.LoginActivityBinding;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
import org.fossasia.openevent.app.main.MainActivity;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.utils.ViewUtils.showView;

public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView, AppCompatEditText.OnEditorActionListener {

    @Inject
    Lazy<ILoginPresenter> presenterProvider;

    @Inject
    HostSelectionInterceptor interceptor;

    private LoginActivityBinding binding;
    private Validator validator;

    private final String DEFAULT_BASE_URL = BuildConfig.DEFAULT_BASE_URL;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        validator = new Validator(binding);

        setSupportActionBar(binding.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attach(this);
        presenter.start();

        setEditTextListener();

        CheckBox checkBoxEnableUrl = binding.loginContent.checkboxEnableUrl;
        TextInputLayout addUrlContainer = binding.loginContent.addUrlContainer;
        checkBoxEnableUrl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxEnableUrl.setTextColor(Color.BLACK);
                addUrlContainer.setVisibility(View.GONE);
            } else {
                checkBoxEnableUrl.setTextColor(Color.parseColor("#808080"));
                addUrlContainer.setVisibility(View.VISIBLE);
            }
        });

        binding.loginContent.btnLogin.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String email = binding.loginContent.emailDropdown.getText().toString();
            String password = binding.loginContent.etPassword.getText().toString();
            String url = binding.loginContent.etBaseUrl.getText().toString().trim();

            presenter.setBaseUrl(interceptor, DEFAULT_BASE_URL, url, checkBoxEnableUrl.isChecked());

            presenter.login(email, password);
        });
    }

    private void setEditTextListener() {
        binding.loginContent.etBaseUrl.setOnEditorActionListener(this);
        binding.loginContent.emailDropdown.setOnEditorActionListener(this);
        binding.loginContent.etPassword.setOnEditorActionListener(this);
    }

    // Lifecycle methods end

    @Override
    protected Lazy<ILoginPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    protected int getLoaderId() {
        return R.layout.login_activity;
    }

    // View Implementation start

    @Override
    public void showProgress(boolean show) {
        showView(binding.loginContent.progressBar, show);
    }

    @Override
    public void onSuccess(String message) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyBoard();
            binding.loginContent.btnLogin.performClick();
            return true;
        }
        return false;
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void attachEmails(Set<String> emails) {
        binding.loginContent.emailDropdown.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(emails))
        );
    }

    // View Implementation end

}
