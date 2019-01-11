package com.eventyay.organizer.core.sponsor.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.databinding.SponsorCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class CreateSponsorFragment extends BaseFragment implements CreateSponsorView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SponsorCreateLayoutBinding binding;
    private Validator validator;
    private CreateSponsorViewModel createSponsorViewModel;
    private long sponsorId;
    private boolean isUpdateSponsor;

    public static CreateSponsorFragment newInstance() {
        return new CreateSponsorFragment();
    }

    public static CreateSponsorFragment newInstance(long sponsorId) {
        Bundle bundle = new Bundle();
        bundle.putLong("sponsor_id", sponsorId);
        CreateSponsorFragment createSponsorFragment = CreateSponsorFragment.newInstance();
        createSponsorFragment.setArguments(bundle);
        return createSponsorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.sponsor_create_layout, container, false);
        createSponsorViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateSponsorViewModel.class);
        validator = new Validator(binding.form);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            sponsorId = bundle.getLong("sponsor_id");
            isUpdateSponsor = sponsorId != -1;
        }

        binding.submit.setOnClickListener(view -> {

            binding.form.name.setText(binding.form.name.getText().toString().trim());
            binding.form.quantity.setText(binding.form.quantity.getText().toString().trim());
            binding.form.price.setText(binding.form.price.getText().toString().trim());

            if (validator.validate())
                if (isUpdateSponsor)
                    createSponsorViewModel.updateSponsor();
                else
                    createSponsorViewModel.createSponsor();

            ViewUtils.hideKeyboard(binding.getRoot());
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        createSponsorViewModel.getProgress().observe(this, this::showProgress);
        createSponsorViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        createSponsorViewModel.getSuccess().observe(this, this::onSuccess);
        createSponsorViewModel.getError().observe(this, this::showError);
        createSponsorViewModel.getSponsorLiveData().observe(this, this::setSponsor);
        binding.setSponsor(createSponsorViewModel.getSponsor());

        validate(binding.form.sponsorSponsorUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.sponsorSponsorLogoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        if (isUpdateSponsor)
            createSponsorViewModel.loadSponsor(sponsorId);
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
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    protected int getTitle() {
        if (isUpdateSponsor)
            return R.string.update_sponsor;
        else
            return R.string.create_sponsor;
    }

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
                if (TextUtils.isEmpty(charSequence.toString().trim())) {
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

    public void dismiss() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setSponsor(Sponsor sponsor) {
        binding.setSponsor(sponsor);
    }

}
