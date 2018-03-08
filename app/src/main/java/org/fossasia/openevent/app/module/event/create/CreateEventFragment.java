package org.fossasia.openevent.app.module.event.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.common.contract.Function;
import org.fossasia.openevent.app.common.utils.core.ValidateUtils;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.EventCreateLayoutBinding;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventPresenter;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class CreateEventFragment extends BaseBottomSheetFragment<ICreateEventPresenter> implements ICreateEventView {

    @Inject
    Lazy<ICreateEventPresenter> presenterProvider;

    private EventCreateLayoutBinding binding;
    private Validator validator;
    private ArrayAdapter<CharSequence> adapter;

    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        OrgaApplication.getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_create_layout, container, false);
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
                getPresenter().createEvent();
        });

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setEvent(getPresenter().getEvent());
        getPresenter().start();

        validate(binding.form.ticketUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.logoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.externalEventUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.originalImageUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.paypalEmailLayout, ValidateUtils::validateEmail, getResources().getString(R.string.email_validation_error));
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
    public void attachCurrencyCodesList(List<String> currencyCodesList) {
        adapter.addAll(currencyCodesList);
        binding.form.currencySpinner.setAdapter(adapter);
    }

    @Override
    public Lazy<ICreateEventPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.event_create_layout;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void close() {
        getActivity().finish();
    }

    @Override
    public List<String> getTimeZoneList() {
        return Arrays.asList(getResources().getStringArray(R.array.timezones));
    }

    @Override
    public void setDefaultTimeZone(int index) {
        binding.form.timezoneSpinner.setSelection(index);
    }

    @Override
    public void setDefaultCurrency(int index) {
        binding.form.currencySpinner.setSelection(index);
    }
}

