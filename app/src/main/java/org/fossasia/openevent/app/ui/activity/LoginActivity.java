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
import org.fossasia.openevent.app.contract.presenter.LoginPresenter;
import org.fossasia.openevent.app.contract.view.LoginView;
import org.fossasia.openevent.app.data.AndroidUtilModel;
import org.fossasia.openevent.app.data.network.api.RetrofitLoginModel;
import org.fossasia.openevent.app.ui.presenter.LoginActivityPresenter;

public class LoginActivity extends AppCompatActivity implements LoginView {
    public static final String TAG = "OpenEventApplication";
    Button btnLogin;
    EditText etEmail;
    EditText etPassword;
    ProgressBar progressBar;

    private LoginPresenter presenter;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        RetrofitLoginModel retrofitLoginModel = new RetrofitLoginModel(new AndroidUtilModel(this));
        presenter = new LoginActivityPresenter(this, retrofitLoginModel);

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
