package org.fossasia.openevent.app.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.openevent.app.utils.ViewUtils.showView;

public class LoginActivity extends AppCompatActivity implements ILoginView, AppCompatEditText.OnEditorActionListener {

    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.etEmail)
    AppCompatEditText etEmail;
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
    ILoginPresenter presenter;

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

        // Notify presenter to attach
        presenter.attach(this);

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

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String url = etBaseUrl.getText().toString().trim();

            presenter.setBaseUrl(interceptor, DEFAULT_BASE_URL, url, checkBoxEnableUrl.isChecked());

            presenter.login(email, password);
        });

    }

    private void setEditTextListener() {
        etBaseUrl.setOnEditorActionListener(this);
        etEmail.setOnEditorActionListener(this);
        etPassword.setOnEditorActionListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Detach view from presenter
        presenter.detach();
    }

    // Lifecycle methods end

    public void startEventActivity(){
        Intent i =  new Intent(LoginActivity.this, EventListActivity.class);
        startActivity(i);
    }

    // View Implementation start

    @Override
    public void showProgressBar(boolean show) {
        showView(progressBar, show);
    }

    @Override
    public void onLoginSuccess() {
        startEventActivity();
        finish();
    }

    @Override
    public void onLoginError(String error) {
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

    // View Implementation end

}
