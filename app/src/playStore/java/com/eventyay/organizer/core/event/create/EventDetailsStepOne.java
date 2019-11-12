package com.eventyay.organizer.core.event.create;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.databinding.EventDetailsStepOneBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class EventDetailsStepOne extends BaseBottomSheetFragment implements EventDetailsStepOneView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateEventViewModel createEventViewModel;
    private EventDetailsStepOneBinding binding;

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
        setupPlacesAutocomplete();
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

    private void setupPlacesAutocomplete() {

        ApplicationInfo ai = null;
        try {
            ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = ai.metaData;
        String mapboxAccessToken = bundle.getString(getString(R.string.mapbox_access_token));

        binding.selectLocationButton.setOnClickListener(view -> {

            if (mapboxAccessToken.equals(getString(R.string.your_access_token))) {
                ViewUtils.showSnackbar(binding.getRoot(), R.string.access_token_required);
                return;
            }

            PlaceAutocompleteFragment autocompleteFragment = PlaceAutocompleteFragment.newInstance(
                mapboxAccessToken, PlaceOptions.builder().backgroundColor(Color.WHITE).build());

            getFragmentManager().beginTransaction()
                .replace(R.id.fragment, autocompleteFragment)
                .addToBackStack(null)
                .commit();

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(CarmenFeature carmenFeature) {
                    Event event = binding.getEvent();
                    event.setLatitude(carmenFeature.center().latitude());
                    event.setLongitude(carmenFeature.center().longitude());
                    event.setLocationName(carmenFeature.placeName());
                    event.setSearchableLocationName(carmenFeature.text());
                    binding.layoutLocationName.setVisibility(View.VISIBLE);
                    binding.locationName.setText(event.getLocationName());
                    getFragmentManager().popBackStack();
                }

                @Override
                public void onCancel() {
                    getFragmentManager().popBackStack();
                }
            });
        });
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
