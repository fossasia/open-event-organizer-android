package org.fossasia.openevent.app.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.EventDetailsStepThreeBinding;
import org.fossasia.openevent.app.utils.ValidateUtils;

import java.util.List;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class EventDetailsStepThree extends BaseBottomSheetFragment implements EventDetailsStepThreeView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EventDetailsStepThreeBinding binding;
    private CreateEventViewModel createEventViewModel;
    private Validator validator;
    private ArrayAdapter<CharSequence> currencyAdapter;
    private ArrayAdapter<CharSequence> paymentCountryAdapter;

    public static Fragment newInstance() {
        return new EventDetailsStepThree();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_step_three, container, false);
        createEventViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CreateEventViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createEventViewModel.getSuccessMessage().observe(this, this::onSuccess);
        createEventViewModel.getErrorMessage().observe(this, this::showError);
        createEventViewModel.getCloseState().observe(this, isClosed -> close());
        createEventViewModel.getEventLiveData().observe(this, this::setPaymentBinding);
        createEventViewModel.getProgress().observe(this, this::showProgress);

        validate(binding.logoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.externalEventUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.originalImageUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.paypalEmailLayout, ValidateUtils::validateEmail, getResources().getString(R.string.email_validation_error));

        getActivity().findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if (validator.validate()) {
                createEventViewModel.createEvent();
            }
        });

        setupSpinners();
        attachCountryList(createEventViewModel.getCountryList());
        attachCurrencyCodesList(createEventViewModel.getCurrencyCodesList());
    }

    private void setupSpinners() {
        currencyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentCountryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        paymentCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.paymentCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = createEventViewModel.onPaymentCountrySelected(adapterView.getItemAtPosition(i).toString());
                setPaymentCurrency(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }

        });
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
    public void setPaymentCurrency(int index) {
        binding.currencySpinner.setSelection(index);
    }

    @Override
    public void attachCountryList(List<String> countryList) {
        paymentCountryAdapter.addAll(countryList);
        binding.paymentCountrySpinner.setAdapter(paymentCountryAdapter);
        binding.paymentCountrySpinner.setSelection(createEventViewModel.getCountryIndex());
    }

    @Override
    public void attachCurrencyCodesList(List<String> currencyCodesList) {
        currencyAdapter.addAll(currencyCodesList);
        binding.currencySpinner.setAdapter(currencyAdapter);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void close() {
        getActivity().finish();
    }

    @Override
    public void setPaymentBinding(Event event) {
        binding.paypalPayment.setChecked(event.canPayByPaypal);
        binding.stripePayment.setChecked(event.canPayByStripe);
        binding.bankPayment.setChecked(event.canPayByBank);
        binding.chequePayment.setChecked(event.canPayByCheque);
        binding.onsitePayment.setChecked(event.canPayOnsite);
        binding.setEvent(event);
    }
}
