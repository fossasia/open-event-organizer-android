package org.fossasia.openevent.app.core.track.update;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class UpdateTrackFragment extends BaseBottomSheetFragment<UpdateTrackPresenter> implements UpdateTrackView {

    private static final String TRACK_ID = "id";
    private ColorPicker colorPicker;

    @Inject
    Lazy<UpdateTrackPresenter> presenterProvider;
    private Validator validator;
    private TrackCreateLayoutBinding binding;
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
        validator = new Validator(binding.form);

        Bundle bundle = getArguments();
        trackId = bundle.getLong(TRACK_ID);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                getPresenter().updateTrack();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().loadTrack(trackId);
    }

    private void setColorPicker() {
        if (colorPicker == null)
            colorPicker = new ColorPicker(getActivity(), getPresenter().getRed(), getPresenter().getGreen(), getPresenter().getBlue());

        binding.form.colorPicker.setOnClickListener(view -> {
            colorPicker.show();
        });

        colorPicker.setCallback(color -> {
            binding.form.trackColor.setText(String.format("#%06X", (0xFFFFFF & color)));
            colorPicker.dismiss();
        });
    }

    @Override
    public void setTrack(Track track) {
        binding.setTrack(track);
        setColorPicker();
    }

    @Override
    public Lazy<UpdateTrackPresenter> getPresenterProvider() {
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
}
