package com.eventyay.organizer.core.organizer.update;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.UpdateOrganizerLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;
import br.com.ilhasoft.support.validation.Validator;


import static com.eventyay.organizer.ui.ViewUtils.showView;

public class UpdateOrganizerInfoFragment extends BaseFragment implements UpdateOrganizerInfoView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private UpdateOrganizerLayoutBinding binding;
    private Validator validator;
    private UpdateOrganizerInfoViewModel updateOrganizerInfoViewModel;
    private AlertDialog saveAlertDialog;

    public static UpdateOrganizerInfoFragment newInstance() {
        return new UpdateOrganizerInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.update_organizer_layout, container, false);
        updateOrganizerInfoViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateOrganizerInfoViewModel.class);
        validator = new Validator(binding.form);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                updateOrganizerInfoViewModel.updateOrganizer();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateOrganizerInfoViewModel.getProgress().observe(this, this::showProgress);
        updateOrganizerInfoViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        updateOrganizerInfoViewModel.getSuccess().observe(this, this::onSuccess);
        updateOrganizerInfoViewModel.getError().observe(this, this::showError);
        updateOrganizerInfoViewModel.getUserLiveData().observe(this, this::setUser);
        updateOrganizerInfoViewModel.loadUser();
        validate(binding.form.holderAvatarUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderTwitterUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderFacebookUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderInstragramUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderThumbnailImageUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderGooglePlusUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.holderIconImageUrl, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
    }

    @Override
    public void validate(TextInputLayout textInputLayout, Function<String, Boolean> validationReference, String errorResponse) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validationReference.apply(charSequence.toString())) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(errorResponse);
                }
                if (TextUtils.isEmpty(charSequence)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing here
            }
        });
    }

    @Override
    public void setUser(User user) {
        binding.setUser(user);
    }

    @Override
    protected int getTitle() {
        return R.string.update;
    }

    public void dismiss() {
        getFragmentManager().popBackStack();
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

    @Override
    public void backPressed() {
        if (saveAlertDialog == null) {
            saveAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialog))
                .setMessage(getString(R.string.save_changes))
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    updateOrganizerInfoViewModel.updateOrganizer();
                    dialog.dismiss();
                    dismiss();
                })
                .setNegativeButton(getString(R.string.discard), (dialog, which) -> {
                    dialog.dismiss();
                    dismiss();
                })
                .create();
        }
        saveAlertDialog.show();
    }

}
