package org.fossasia.openevent.app.core.track.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.databinding.TrackCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateTrackFragment extends BaseBottomSheetFragment<CreateTrackPresenter> implements CreateTrackView {

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
            if (validator.validate())
                getPresenter().createTrack();
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
    protected Lazy<CreateTrackPresenter> getPresenterProvider() {
        return presenterProvider;
    }
}
