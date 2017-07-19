package org.fossasia.openevent.app.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseActivity;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
import org.fossasia.openevent.app.main.MainActivity;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;

import static org.fossasia.openevent.app.utils.ViewUtils.showView;

public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView, AppCompatEditText.OnEditorActionListener {

    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.etEmail)
    TextInputLayout etEmail;
    @BindView(R.id.email_dropdown)
    AutoCompleteTextView autoCompleteEmail;
    @BindView(R.id.etPassword)
    AppCompatEditText etPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.checkboxEnableUrl)
    CheckBox checkBoxEnableUrl;
    @BindView(R.id.addUrlContainer)
    View addUrlContainer;
    @BindView(R.id.etBaseUrl)
    AppCompatEditText etBaseUrl;

    @Inject
    Lazy<ILoginPresenter> presenterProvider;

    @Inject
    HostSelectionInterceptor interceptor;

    private final String DEFAULT_BASE_URL = BuildConfig.DEFAULT_BASE_URL;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attach(this);
        presenter.start();

        setEditTextListener();

        checkBoxEnableUrl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxEnableUrl.setTextColor(Color.BLACK);
                addUrlContainer.setVisibility(View.GONE);
            } else {
                checkBoxEnableUrl.setTextColor(Color.parseColor("#808080"));
                addUrlContainer.setVisibility(View.VISIBLE);
            }
        });

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getEditText().getText().toString();
            String password = etPassword.getText().toString();
            String url = etBaseUrl.getText().toString().trim();

            presenter.setBaseUrl(interceptor, DEFAULT_BASE_URL, url, checkBoxEnableUrl.isChecked());

            presenter.login(email, password);
        });
    }

    private void setEditTextListener() {
        etBaseUrl.setOnEditorActionListener(this);
        etEmail.getEditText().setOnEditorActionListener(this);
        etPassword.setOnEditorActionListener(this);
    }

    // Lifecycle methods end

    @Override
    protected Lazy<ILoginPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    protected int getLoaderId() {
        return R.layout.activity_login;
    }

    // View Implementation start

    @Override
    public void showProgress(boolean show) {
        showView(progressBar, show);
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
            btnLogin.performClick();
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
        autoCompleteEmail.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
            new ArrayList<String>(emails)));
    }

    // View Implementation end

}
