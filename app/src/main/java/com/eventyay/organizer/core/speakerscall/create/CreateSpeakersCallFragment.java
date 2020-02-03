package com.eventyay.organizer.core.speakerscall.create;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.databinding.SpeakersCallCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class CreateSpeakersCallFragment extends BaseFragment implements CreateSpeakersCallView {

    private static final String SPEAKERS_CALL_UPDATE = "speakers_update";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateSpeakersCallViewModel createSpeakersCallViewModel;

    private SpeakersCallCreateLayoutBinding binding;
    private Validator validator;
    private long eventId;
    private boolean isSpeakersCallUpdating;

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
        binding = DataBindingUtil.inflate(localInflater, R.layout.speakers_call_create_layout, container, false);

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
        setUpSpinner();
        if (isSpeakersCallUpdating) {
            createSpeakersCallViewModel.loadSpeakersCall(eventId, false);
        } else {
            createSpeakersCallViewModel.initialize();
        }
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> rolesAdapter = ArrayAdapter
            .createFromResource(getActivity(), R.array.privacy_array, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.form.privacy.setAdapter(rolesAdapter);
        binding.form.privacy.setSelection(((ArrayAdapter<CharSequence>)binding.form.privacy.getAdapter()).getPosition("public"));

    }

    public void showSpeakersCall(SpeakersCall speakersCall) {
        binding.setSpeakersCall(speakersCall);
        binding.form.privacy.setSelection(((ArrayAdapter<CharSequence>)binding.form.privacy.getAdapter()).getPosition(speakersCall.privacy));
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

    @Override
    protected int getTitle() {
        if (isSpeakersCallUpdating) {
            return R.string.update_speakers_call;
        } else {
            return R.string.create_speakers_call;
        }
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
