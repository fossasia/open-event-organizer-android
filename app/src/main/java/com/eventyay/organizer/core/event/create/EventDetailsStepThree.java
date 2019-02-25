package com.eventyay.organizer.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.image.ImageUrl;
import com.eventyay.organizer.databinding.EventDetailsStepThreeBinding;
import com.eventyay.organizer.utils.Utils;
import com.eventyay.organizer.utils.ValidateUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.eventyay.organizer.ui.ViewUtils.showView;

public class EventDetailsStepThree extends BaseBottomSheetFragment implements EventDetailsStepThreeView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EventDetailsStepThreeBinding binding;
    private CreateEventViewModel createEventViewModel;
    private Validator validator;

    private static final int LOGO_IMAGE_CHOOSER_REQUEST_CODE = 1;
    private static final int ORIGINAL_IMAGE_CHOOSER_REQUEST_CODE = 2;

    public static Fragment newInstance() {
        return new EventDetailsStepThree();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_details_step_three, container, false);
        createEventViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateEventViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setEvent(createEventViewModel.getEvent());
        createEventViewModel.getSuccessMessage().observe(this, this::onSuccess);
        createEventViewModel.getErrorMessage().observe(this, this::showError);
        createEventViewModel.getCloseState().observe(this, isClosed -> close());
        createEventViewModel.getProgress().observe(this, this::showProgress);
        createEventViewModel.getEvent().isTaxEnabled = true;

        createEventViewModel.getLogoUrlLiveData().observe(this, this::setLogoImageUrl);
        createEventViewModel.getImageUrlLiveData().observe(this, this::setOriginalImageUrl);

        ValidateUtils.validate(binding.logoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        ValidateUtils.validate(binding.externalEventUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        ValidateUtils.validate(binding.originalImageUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));

        binding.logoImageLayout.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOGO_IMAGE_CHOOSER_REQUEST_CODE);
        });

        binding.originalImageLayout.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ORIGINAL_IMAGE_CHOOSER_REQUEST_CODE);
        });

        getActivity().findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if (validator.validate()) {
                createEventViewModel.createEvent();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                String encodedImage = Utils.encodeImage(getActivity(), bitmap, selectedImageUri);
                ImageData imageData = new ImageData(encodedImage);

                if (requestCode == LOGO_IMAGE_CHOOSER_REQUEST_CODE) {
                    createEventViewModel.uploadLogo(imageData);
                    binding.logoImage.setImageBitmap(bitmap);
                } else if (requestCode == ORIGINAL_IMAGE_CHOOSER_REQUEST_CODE) {
                    createEventViewModel.uploadImage(imageData);
                    binding.originalImage.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                Timber.e(e, "File not found");
                Toast.makeText(getActivity(), "File not found. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setLogoImageUrl(ImageUrl imageUrl) {
        binding.logoUrl.setText(imageUrl.getUrl());
    }

    private void setOriginalImageUrl(ImageUrl imageUrl) {
        binding.originalImageUrl.setText(imageUrl.getUrl());
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

}
