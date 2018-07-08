package org.fossasia.openevent.app.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.EventDetailsStepOneBinding;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class EventDetailsStepOne extends BaseBottomSheetFragment implements EventDetailsStepOneView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateEventViewModel createEventViewModel;
    private EventDetailsStepOneBinding binding;
    private static final int PLACE_PICKER_REQUEST = 1;
    private final GoogleApiAvailability googleApiAvailabilityInstance = GoogleApiAvailability.getInstance();

    public static EventDetailsStepOne newInstance() {
        return new EventDetailsStepOne();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_step_one, container, false);
        createEventViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CreateEventViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setEvent(createEventViewModel.getEvent());
        int timezoneIndex = createEventViewModel.setTimeZoneList(getTimeZoneList());
        setupSpinner();
        setDefaultTimeZone(timezoneIndex);
        setupPlacePicker();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> timezoneAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        timezoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezoneAdapter.addAll(getTimeZoneList());
        binding.timezoneSpinner.setAdapter(timezoneAdapter);

        binding.timezoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeZone = parent.getItemAtPosition(position).toString();
                createEventViewModel.getEvent().setTimezone(timeZone);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String timeZone = parent.toString();
                createEventViewModel.getEvent().setTimezone(timeZone);
            }
        });
    }

    private void setupPlacePicker() {
        //check if there's an google places API key
        try {
            ApplicationInfo ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String placesApiKey = bundle.getString("com.google.android.geo.API_KEY");
            if ("YOUR_API_KEY".equals(placesApiKey)) {
                Timber.d("Add Google Places API key in AndroidManifest.xml file to use Place Picker.");
                binding.buttonPlacePicker.setVisibility(View.GONE);
                binding.layoutLatitude.setVisibility(View.VISIBLE);
                binding.layoutLongitude.setVisibility(View.VISIBLE);
                showLocationLayouts();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Package name not found");
        }

        binding.buttonPlacePicker.setOnClickListener(view -> {
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

            binding.locationName.setText(place.getAddress());
            binding.searchableLocationName.setText(
                createEventViewModel.getSearchableLocationName(place.getAddress().toString()));
        }
    }

    private void showLocationLayouts() {
        binding.layoutSearchableLocation.setVisibility(View.VISIBLE);
        binding.layoutLocationName.setVisibility(View.VISIBLE);
    }

    @Override
    public List<String> getTimeZoneList() {
        return Arrays.asList(getResources().getStringArray(R.array.timezones));
    }

    @Override
    public void setDefaultTimeZone(int index) {
        binding.timezoneSpinner.setSelection(index);
    }

}
