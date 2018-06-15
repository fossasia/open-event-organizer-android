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
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.databinding.SpeakersCallCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateSpeakersCallFragment extends BaseBottomSheetFragment implements CreateSpeakersCallView {

    private static final String SPEAKERS_CALL_UPDATE = "speakers_update";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateSpeakersCallViewModel createSpeakersCallViewModel;

    private SpeakersCallCreateLayoutBinding binding;
    private Validator validator;
    private long eventId;
    private boolean isSpeakersCallUpdating = false;

    public static CreateSpeakersCallFragment newInstance(long eventId) {
        CreateSpeakersCallFragment fragment = new CreateSpeakersCallFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateSpeakersCallFragment newInstance(long eventId, boolean updateSpeakersCall) {
        CreateSpeakersCallFragment fragment = new CreateSpeakersCallFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        args.putBoolean(SPEAKERS_CALL_UPDATE, updateSpeakersCall);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
            isSpeakersCallUpdating = getArguments().getBoolean(SPEAKERS_CALL_UPDATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.speakers_call_create_layout, container, false);

        createSpeakersCallViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateSpeakersCallViewModel.class);
        validator = new Validator(binding.form);

        binding.submit.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            ViewUtils.hideKeyboard(view);
            if (isSpeakersCallUpdating) {
                createSpeakersCallViewModel.updateSpeakersCall(eventId);
            } else {
                createSpeakersCallViewModel.createSpeakersCall(eventId);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createSpeakersCallViewModel.getSpeakersCall().observe(this, this::showSpeakersCall);
        createSpeakersCallViewModel.getProgress().observe(this, this::showProgress);
        createSpeakersCallViewModel.getError().observe(this, this::showError);
        createSpeakersCallViewModel.getSuccess().observe(this, this::onSuccess);

        if (isSpeakersCallUpdating) {
            createSpeakersCallViewModel.loadSpeakersCall(eventId, false);
        } else {
            createSpeakersCallViewModel.initialize();
        }
    }

    public void showSpeakersCall(SpeakersCall speakersCall) {
        binding.setSpeakersCall(speakersCall);
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
