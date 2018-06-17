package org.fossasia.openevent.app.core.session.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SessionCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateSessionFragment extends BaseFragment<CreateSessionPresenter> implements CreateSessionView {

    @Inject
    Lazy<CreateSessionPresenter> presenterProvider;

    private SessionCreateLayoutBinding binding;
    private Validator validator;
    public static final String TRACK_KEY = "track";
    private static final String SESSION_KEY = "session_id";

    private boolean isSessionUpdating;
    private long trackId;
    private long eventId;
    private long sessionId = -1;

    public static CreateSessionFragment newInstance(long trackId, long eventId) {
        CreateSessionFragment fragment = new CreateSessionFragment();
        Bundle args = new Bundle();
        args.putLong(TRACK_KEY, trackId);
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateSessionFragment newInstance(long trackId, long eventId, long sessionId) {
        CreateSessionFragment fragment = new CreateSessionFragment();
        Bundle args = new Bundle();
        args.putLong(TRACK_KEY, trackId);
        args.putLong(MainActivity.EVENT_KEY, eventId);
        args.putLong(SESSION_KEY, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            trackId = getArguments().getLong(TRACK_KEY);
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
            sessionId = getArguments().getLong(SESSION_KEY);

            if (sessionId == -1 || sessionId == 0) {
                isSessionUpdating = false;
            } else {
                isSessionUpdating = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.session_create_layout, container, false);
        validator = new Validator(binding.form);

        binding.sessionCreate.setOnClickListener(view -> {
            if (validator.validate()) {
                if (isSessionUpdating) {
                    getPresenter().updateSession(trackId, eventId);
                } else {
                    getPresenter().createSession(trackId, eventId);
                }
            }
        });

        setUpSpinner();
        return binding.getRoot();
    }

    private void setUpSpinner() {
        ArrayAdapter<String> sessionStateAdapter;
        sessionStateAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getPresenter().getSessionStateList());
        sessionStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.form.spinner.setAdapter(sessionStateAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setSession(getPresenter().getSession());

        if (isSessionUpdating) {
            binding.form.sessionFormTitle.setText(getResources().getString(R.string.update_session));
            getPresenter().loadSession(sessionId);
        } else {
            binding.form.sessionFormTitle.setText(getResources().getString(R.string.create_session));
        }

        validate(binding.form.slidesUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.audioUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.videoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.signupUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
    }

    @Override
    public void validate(TextInputLayout textInputLayout, Function<String, Boolean> validationReference, String errorResponse) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validationReference.apply(charSequence.toString())) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(errorResponse);
                }
                if (TextUtils.isEmpty(charSequence)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing here
            }
        });
    }

    @Override
    public void setSession(Session session) {
        binding.setSession(session);
    }

    @Override
    protected int getTitle() {
        return R.string.create_session;
    }

    @Override
    public Lazy<CreateSessionPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }

}
