package com.eventyay.organizer.core.organizer.update;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.eventyay.organizer.data.event.ImageData;
import com.eventyay.organizer.data.event.ImageUrl;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.UpdateOrganizerLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.Utils;
import com.eventyay.organizer.utils.ValidateUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.inject.Inject;
import br.com.ilhasoft.support.validation.Validator;
import timber.log.Timber;


import static android.app.Activity.RESULT_OK;
import static com.eventyay.organizer.ui.ViewUtils.showView;

public class UpdateOrganizerInfoFragment extends BaseFragment implements UpdateOrganizerInfoView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private UpdateOrganizerLayoutBinding binding;
    private Validator validator;
    private UpdateOrganizerInfoViewModel updateOrganizerInfoViewModel;
    private AlertDialog saveAlertDialog;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 234;

    public static UpdateOrganizerInfoFragment newInstance() {
        return new UpdateOrganizerInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding = DataBindingUtil.inflate(localInflater, R.layout.update_organizer_layout, container, false);
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

        binding.form.changeProfile.setOnClickListener(view -> {
            selectImage();
        });

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
        updateOrganizerInfoViewModel.getImageUrlLiveData().observe(this, this::setImageUrl);
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

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                String encodedImage = Utils.encodeImage(getActivity(), bitmap, filePath);
                ImageData imageData = new ImageData(encodedImage);
                updateOrganizerInfoViewModel.uploadImage(imageData);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                binding.form.ProfileImage.setBackground(drawable);
            } catch (FileNotFoundException e) {
                Timber.e(e, "File not found");
                Toast.makeText(getActivity(), "File not found. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void setUser(User user) {
        binding.setUser(user);
    }

    private void setImageUrl(ImageUrl imageUrl) {
        binding.form.avatarUrl.setText(imageUrl.getUrl());
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
