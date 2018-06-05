package org.fossasia.openevent.app.core.event.create;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.EventCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.ValidateUtils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateEventFragment extends BaseBottomSheetFragment<CreateEventPresenter> implements CreateEventView {

    @Inject
    Lazy<CreateEventPresenter> presenterProvider;

    private EventCreateLayoutBinding binding;
    private Validator validator;
    private ArrayAdapter<CharSequence> currencyAdapter;
    private ArrayAdapter<CharSequence> paymentCountryAdapter;
    private long eventId = -1;
    private boolean isUpdateEvent;

    private static final int PLACE_PICKER_REQUEST = 1;
    private final GoogleApiAvailability googleApiAvailabilityInstance = GoogleApiAvailability.getInstance();

    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }

    public static CreateEventFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(CreateEventActivity.EVENT_ID, id);
        CreateEventFragment createEventFragment = new CreateEventFragment();
        createEventFragment.setArguments(bundle);
        return createEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_create_layout, container, false);
        validator = new Validator(binding.form);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            eventId = bundle.getLong(CreateEventActivity.EVENT_ID);
            isUpdateEvent = eventId != -1;
        }

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate()) {
                if (isUpdateEvent)
                    getPresenter().updateEvent();
                else
                    getPresenter().createEvent();
            }
        });

        setupSpinners();

        setupPlacePicker();

        return binding.getRoot();
    }

    private void setupSpinners() {
        currencyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentCountryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        paymentCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.form.paymentCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getPresenter().onPaymentCountrySelected(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }

        });
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
        if (isUpdateEvent) {
            binding.form.createEventTitle.setText(getResources().getString(R.string.update_event));
            getPresenter().loadEvents(eventId);
        } else {
            binding.form.createEventTitle.setText(getResources().getString(R.string.create_event));
        }

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
        binding.form.currencySpinner.setSelection(index);
    }

    @Override
    public void attachCountryList(List<String> countryList) {
        paymentCountryAdapter.addAll(countryList);
        binding.form.paymentCountrySpinner.setAdapter(paymentCountryAdapter);
    }

    @Override
    public void attachCurrencyCodesList(List<String> currencyCodesList) {
        currencyAdapter.addAll(currencyCodesList);
        binding.form.currencySpinner.setAdapter(currencyAdapter);
    }

    @Override
    public Lazy<CreateEventPresenter> getPresenterProvider() {
        return presenterProvider;
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
    public void setDefaultCountry(int index) {
        binding.form.paymentCountrySpinner.setSelection(index);
    }

    private void setupPlacePicker() {
        //check if there's an google places API key
        try {
            ApplicationInfo ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String placesApiKey = bundle.getString("com.google.android.geo.API_KEY");
            if ("YOUR_API_KEY".equals(placesApiKey)) {
                Timber.d("Add Google Places API key in AndroidManifest.xml file to use Place Picker.");
                binding.form.buttonPlacePicker.setVisibility(View.GONE);
                binding.form.layoutLatitude.setVisibility(View.VISIBLE);
                binding.form.layoutLongitude.setVisibility(View.VISIBLE);
                showLocationLayouts();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Package name not found");
        }

        binding.form.buttonPlacePicker.setOnClickListener(view -> {
            int errorCode = googleApiAvailabilityInstance.isGooglePlayServicesAvailable(getContext());
            if (errorCode == ConnectionResult.SUCCESS) {
                //SUCCESS
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Timber.d(e, "GooglePlayServicesRepairable");
                } catch (GooglePlayServicesNotAvailableException e) {
                    Timber.d("GooglePlayServices NotAvailable => Updating or Unauthentic");
                }
            } else if (googleApiAvailabilityInstance.isUserResolvableError(errorCode)) {
                //SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED
                googleApiAvailabilityInstance.getErrorDialog(
                    getActivity(), errorCode, PLACE_PICKER_REQUEST, (dialog) -> {
                        showLocationLayouts();
                    });
            } else {
                //SERVICE_UPDATING, SERVICE_INVALID - can't use place picker - must enter manually
                showLocationLayouts();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            //once place is picked from map, make location fields visible for confirmation by user
            showLocationLayouts();
            //set event attributes
            Place place = PlacePicker.getPlace(getActivity(), data);
            Event event = binding.getEvent();
            event.latitude = place.getLatLng().latitude;
            event.longitude = place.getLatLng().longitude;
            //auto-complete location fields for confirmation by user

            binding.form.locationName.setText(place.getAddress());
            binding.form.searchableLocationName.setText(
                getPresenter().getSearchableLocationName(place.getAddress().toString())
            );
        }
    }

    private void showLocationLayouts() {
        binding.form.layoutSearchableLocation.setVisibility(View.VISIBLE);
        binding.form.layoutLocationName.setVisibility(View.VISIBLE);
    }

    @Override
    public void setEvent(Event event) {
        binding.setEvent(event);
    }
}
