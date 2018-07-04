package org.fossasia.openevent.app.core.sponsor.create;

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

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateSponsorFragment extends BaseFragment<CreateSponsorPresenter> implements CreateSponsorView {

    @Inject
    Lazy<CreateSponsorPresenter> presenterProvider;

    private SponsorCreateLayoutBinding binding;
    private Validator validator;
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
        validator = new Validator(binding.form);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            sponsorId = bundle.getLong("sponsor_id");
            isUpdateSponsor = sponsorId != -1;
        }

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                if (isUpdateSponsor)
                    getPresenter().updateSponsor();
                else
                    getPresenter().createSponsor();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setSponsor(getPresenter().getSponsor());

        validate(binding.form.sponsorSponsorUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.sponsorSponsorLogoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        if (isUpdateSponsor)
           getPresenter().loadSponsor(sponsorId);
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

    @Override
    protected Lazy<CreateSponsorPresenter> getPresenterProvider() {
        return presenterProvider;
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
    public void dismiss() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setSponsor(Sponsor sponsor) {
        binding.setSponsor(sponsor);
    }

}
