package org.fossasia.openevent.app.core.session.create;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.databinding.SessionCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

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
    private long trackId;
    private long eventId;

    public static CreateSessionFragment newInstance(long trackId, long eventId) {
        CreateSessionFragment fragment = new CreateSessionFragment();
        Bundle args = new Bundle();
        args.putLong(TRACK_KEY, trackId);
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            trackId = getArguments().getLong(TRACK_KEY);
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.session_create_layout, container, false);
        validator = new Validator(binding.form);

        binding.sessionCreate.setOnClickListener(view -> {
            if (validator.validate()) {
                getPresenter().createSession(trackId, eventId);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setSession(getPresenter().getSession());
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
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
