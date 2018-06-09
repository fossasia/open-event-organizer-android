package org.fossasia.openevent.app.core.speakerscall.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.databinding.SpeakersCallCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateSpeakersCallFragment extends BaseBottomSheetFragment implements CreateSpeakersCallView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateSpeakersCallViewModel createSpeakersCallViewModel;

    private SpeakersCallCreateLayoutBinding binding;
    private Validator validator;
    private long eventId;

    public static CreateSpeakersCallFragment newInstance(long eventId) {
        CreateSpeakersCallFragment fragment = new CreateSpeakersCallFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.speakers_call_create_layout, container, false);

        createSpeakersCallViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateSpeakersCallViewModel.class);
        validator = new Validator(binding.form);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        createSpeakersCallViewModel.initialize();
        createSpeakersCallViewModel.getProgress().observe(this, this::showProgress);
        createSpeakersCallViewModel.getError().observe(this, this::showError);
        createSpeakersCallViewModel.getSuccess().observe(this, this::onSuccess);

        binding.setSpeakersCall(createSpeakersCallViewModel.getSpeakersCall());

        binding.submit.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            ViewUtils.hideKeyboard(view);
            createSpeakersCallViewModel.createSpeakersCall(eventId);
        });
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
        dismiss();
    }
}
