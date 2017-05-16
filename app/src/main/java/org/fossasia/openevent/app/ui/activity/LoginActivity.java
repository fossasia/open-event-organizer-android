package org.fossasia.openevent.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.contract.model.LoginModel;
import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.contract.presenter.LoginPresenter;
import org.fossasia.openevent.app.contract.view.LoginView;
import org.fossasia.openevent.app.data.AndroidUtilModel;
import org.fossasia.openevent.app.data.network.api.RetrofitLoginModel;
import org.fossasia.openevent.app.ui.presenter.LoginActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginView {
    public static final String TAG = "OpenEventApplication";

    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private LoginPresenter presenter;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        UtilModel utilModel = new AndroidUtilModel(this);
        LoginModel loginModel = new RetrofitLoginModel(utilModel);
        presenter = new LoginActivityPresenter(this, loginModel, utilModel);

        // Notify presenter to attach
        presenter.attach();

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Login Button Clicked");

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            presenter.login(email, password);
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Detach view from presenter
        presenter.detach();
    }

    // Lifecycle methods end

    public void startEventActivity(){
        Intent i =  new Intent(LoginActivity.this, EventsActivity.class);
        startActivity(i);
    }

    // View Implementation start

    @Override
    public void showProgressBar(boolean show) {
        int mode = View.GONE;

        if(show)
            mode = View.VISIBLE;

        progressBar.setVisibility(mode);
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

    // View Implementation end

}
