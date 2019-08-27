package com.eventyay.organizer.core.event.copyright.update;

import static com.eventyay.organizer.ui.ViewUtils.showView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import br.com.ilhasoft.support.validation.Validator;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.databinding.CopyrightCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;
import javax.inject.Inject;

public class UpdateCopyrightFragment extends BaseFragment implements UpdateCopyrightView {

    private static final String EVENT_ID = "id";

    @Inject ViewModelProvider.Factory viewModelFactory;

    private Validator validator;
    private CopyrightCreateLayoutBinding binding;
    private UpdateCopyrightViewModel updateCopyrightViewModel;
    private long copyrightId;

    public static UpdateCopyrightFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(EVENT_ID, id);
        UpdateCopyrightFragment updateCopyrightFragment = new UpdateCopyrightFragment();
        updateCopyrightFragment.setArguments(bundle);
        return updateCopyrightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding =
                DataBindingUtil.inflate(
                        inflater, R.layout.copyright_create_layout, container, false);
        validator = new Validator(binding.form);
        updateCopyrightViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(UpdateCopyrightViewModel.class);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        copyrightId = bundle.getLong(EVENT_ID);

        binding.submit.setOnClickListener(
                view -> {
                    if (validator.validate()) updateCopyrightViewModel.updateCopyright();
                });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCopyrightViewModel.getProgress().observe(this, this::showProgress);
        updateCopyrightViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        updateCopyrightViewModel.getSuccess().observe(this, this::onSuccess);
        updateCopyrightViewModel.getError().observe(this, this::showError);
        updateCopyrightViewModel.getCopyrightLiveData().observe(this, this::setCopyright);
        updateCopyrightViewModel.loadCopyright(copyrightId);
        binding.setCopyright(updateCopyrightViewModel.getCopyright());
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setCopyright(Copyright copyright) {
        binding.setCopyright(copyright);
    }

    @Override
    protected int getTitle() {
        return R.string.edit_copyright;
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
