package com.eventyay.organizer.core.track.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.TrackCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class CreateTrackFragment extends BaseFragment<CreateTrackPresenter> implements CreateTrackView {

    @Inject
    Lazy<CreateTrackPresenter> presenterProvider;

    private TrackCreateLayoutBinding binding;
    private Validator validator;
    private ColorPicker colorPickerDialog;

    public static CreateTrackFragment newInstance() {
        return new CreateTrackFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.track_create_layout, container, false);
        validator = new Validator(binding.form);

        binding.submit.setOnClickListener(view -> {

            binding.form.trackName.setText(binding.form.trackName.getText().toString().trim());
            binding.form.trackDescription.setText(binding.form.trackDescription.getText().toString().trim());
            binding.form.trackColor.setText(binding.form.trackColor.getText().toString().trim());

            if (validator.validate())
                getPresenter().createTrack();

            ViewUtils.hideKeyboard(binding.getRoot());
        });

        return binding.getRoot();
    }

   @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().start();
        setColorPicker();
        binding.setTrack(getPresenter().getTrack());
    }

    private void setColorPicker() {
        if (colorPickerDialog == null)
            colorPickerDialog = new ColorPicker(getActivity(), getPresenter().getRed(), getPresenter().getGreen(), getPresenter().getBlue());

        binding.form.colorPicker.setBackgroundColor(getPresenter().getColorRGB());

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
    protected Lazy<CreateTrackPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
