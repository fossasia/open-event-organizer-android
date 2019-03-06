package com.eventyay.organizer.core.track.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.data.tracks.Track;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.TrackCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class CreateTrackFragment extends BaseFragment implements CreateTrackView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TrackCreateLayoutBinding binding;
    private Validator validator;
    private CreateTrackViewModel createTrackViewModel;
    private ColorPicker colorPickerDialog;

    public static CreateTrackFragment newInstance() {
        return new CreateTrackFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.track_create_layout, container, false);
        binding.form.trackName.requestFocus();
        ViewUtils.showKeyboard(getContext());
        createTrackViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateTrackViewModel.class);
        validator = new Validator(binding.form);

        binding.submit.setOnClickListener(view -> {
            binding.form.trackName.setText(binding.form.trackName.getText().toString().trim());
            binding.form.trackDescription.setText(binding.form.trackDescription.getText().toString().trim());
            binding.form.trackColor.setText(binding.form.trackColor.getText().toString().trim());

            if (validator.validate())
                createTrackViewModel.createTrack();

            ViewUtils.hideKeyboard(binding.getRoot());
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createTrackViewModel.getProgress().observe(this, this::showProgress);
        createTrackViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        createTrackViewModel.getSuccess().observe(this, this::onSuccess);
        createTrackViewModel.getError().observe(this, this::showError);
        createTrackViewModel.getTrackLiveData().observe(this, this::setTrack);
        binding.setTrack(createTrackViewModel.getTrack());
        setColorPicker();
    }

    private void setColorPicker() {
        if (colorPickerDialog == null)
            colorPickerDialog = new ColorPicker(getActivity(), createTrackViewModel.getRed(), createTrackViewModel.getGreen(), createTrackViewModel.getBlue());

        binding.form.colorPicker.setBackgroundColor(createTrackViewModel.getColorRGB());

        binding.form.colorPicker.setOnClickListener(view -> {
            colorPickerDialog.show();
        });

        colorPickerDialog.setCallback(color -> {
            binding.form.trackColor.setText(String.format("#%06X", 0xFFFFFF & color));
            binding.form.colorPicker.setBackgroundColor(color);
            colorPickerDialog.dismiss();
        });
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    protected int getTitle() {
        return R.string.create_track;
    }

    @Override
    public void setTrack(Track track) {
        binding.setTrack(track);
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
