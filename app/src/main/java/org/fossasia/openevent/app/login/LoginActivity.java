package org.fossasia.openevent.app.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.events.EventsActivity;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.openevent.app.utils.AndroidUtils.showView;

public class LoginActivity extends AppCompatActivity implements ILoginView {
    public static final String TAG = "OpenEventApplication";

    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ILoginPresenter presenter;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        IUtilModel utilModel = new UtilModel(this);
        presenter = new LoginPresenter(this, new LoginModel(utilModel), utilModel);

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

    // View Implementation end

}
