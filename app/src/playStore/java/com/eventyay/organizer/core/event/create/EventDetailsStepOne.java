package com.eventyay.organizer.core.event.create;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class EventDetailsStepOne extends BaseBottomSheetFragment
        implements EventDetailsStepOneView {

    @Inject ViewModelProvider.Factory viewModelFactory;

    private CreateEventViewModel createEventViewModel;
    private EventDetailsStepOneBinding binding;

    public static EventDetailsStepOne newInstance() {
        return new EventDetailsStepOne();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding =
                DataBindingUtil.inflate(
                        inflater, R.layout.event_details_step_one, container, false);
        createEventViewModel =
                ViewModelProviders.of(getActivity(), viewModelFactory)
                        .get(CreateEventViewModel.class);
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
        ArrayAdapter<CharSequence> timezoneAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        timezoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezoneAdapter.addAll(getTimeZoneList());
        binding.timezoneSpinner.setAdapter(timezoneAdapter);

        binding.timezoneSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
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
            ai =
                    getContext()
                            .getPackageManager()
                            .getApplicationInfo(
                                    getContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = ai.metaData;
        String placesApiKey = bundle.getString("com.google.android.geo.API_KEY");

        Places.initialize(getActivity().getApplicationContext(), placesApiKey);

        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment)
                        getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        Timber.d(place.getAddress());
                        Event event = binding.getEvent();
                        event.latitude = place.getLatLng().latitude;
                        event.longitude = place.getLatLng().longitude;
                        event.locationName = place.getAddress();
                        event.searchableLocationName = place.getName();
                    }

                    @Override
                    public void onError(Status status) {
                        ViewUtils.showSnackbar(binding.getRoot(), status.getStatusMessage());
                    }
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
