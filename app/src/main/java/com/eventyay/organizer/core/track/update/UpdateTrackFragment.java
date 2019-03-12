package com.eventyay.organizer.core.track.update;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import top.defaults.colorpicker.ColorPickerPopup;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.databinding.TrackCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class UpdateTrackFragment extends BaseBottomSheetFragment implements UpdateTrackView {

    private static final String TRACK_ID = "id";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Validator validator;
    private TrackCreateLayoutBinding binding;
    private UpdateTrackViewModel updateTrackViewModel;
    private long trackId;

    public static UpdateTrackFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(TRACK_ID, id);
        UpdateTrackFragment updateTrackFragment = new UpdateTrackFragment();
        updateTrackFragment.setArguments(bundle);
        return updateTrackFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.track_create_layout, container, false);
        updateTrackViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateTrackViewModel.class);
        validator = new Validator(binding.form);

        Bundle bundle = getArguments();
        trackId = bundle.getLong(TRACK_ID);

        binding.submit.setOnClickListener(view -> {
            binding.form.trackName.setText(binding.form.trackName.getText().toString().trim());
            binding.form.trackDescription.setText(binding.form.trackDescription.getText().toString().trim());
            binding.form.trackColor.setText(binding.form.trackColor.getText().toString().trim());

            if (validator.validate())
                updateTrackViewModel.updateTrack();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrackViewModel.getProgress().observe(this, this::showProgress);
        updateTrackViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        updateTrackViewModel.getSuccess().observe(this, this::onSuccess);
        updateTrackViewModel.getError().observe(this, this::showError);
        updateTrackViewModel.getTrackLiveData().observe(this, this::setTrack);
        updateTrackViewModel.loadTrack(trackId);
        binding.setTrack(updateTrackViewModel.getTrack());
    }

    private void setColorPicker() {
        binding.form.colorPicker.setBackgroundColor(updateTrackViewModel.getColorRGB());

        binding.form.colorPicker.setOnClickListener(view -> {
            new ColorPickerPopup.Builder(getContext())
                .initialColor(updateTrackViewModel.getColorRGB()) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(false) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(getView(), new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        binding.form.trackColor.setText(String.format("#%06X", 0xFFFFFF & color));
                        binding.form.colorPicker.setBackgroundColor(color);
                    }
                });
        });
    }

    @Override
    public void setTrack(Track track) {
        binding.setTrack(track);
        setColorPicker();
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
}
