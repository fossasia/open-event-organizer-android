package com.eventyay.organizer.core.event.copyright;

import static com.eventyay.organizer.ui.ViewUtils.showView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import br.com.ilhasoft.support.validation.Validator;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.CopyrightCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;
import com.google.android.material.textfield.TextInputLayout;
import javax.inject.Inject;

public class CreateCopyrightFragment extends BaseFragment implements CreateCopyrightView {

    @Inject ViewModelProvider.Factory viewModelFactory;

    private CopyrightCreateLayoutBinding binding;
    private Validator validator;
    private CreateCopyrightViewModel createCopyrightViewModel;

    public static CreateCopyrightFragment newInstance() {
        return new CreateCopyrightFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper =
                new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =
                DataBindingUtil.inflate(
                        localInflater, R.layout.copyright_create_layout, container, false);
        createCopyrightViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(CreateCopyrightViewModel.class);
        validator = new Validator(binding.form);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        binding.submit.setOnClickListener(
                view -> {
                    if (validator.validate()) createCopyrightViewModel.createCopyright();
                });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        createCopyrightViewModel.getProgress().observe(this, this::showProgress);
        createCopyrightViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        createCopyrightViewModel.getSuccess().observe(this, this::onSuccess);
        createCopyrightViewModel.getError().observe(this, this::showError);
        binding.setCopyright(createCopyrightViewModel.getCopyright());

        validate(
                binding.form.copyrightHolderUrlLayout,
                ValidateUtils::validateUrl,
                getResources().getString(R.string.url_validation_error));
        validate(
                binding.form.copyrightLicenceUrlLayout,
                ValidateUtils::validateUrl,
                getResources().getString(R.string.url_validation_error));
        validate(
                binding.form.copyrightLogoUrlLayout,
                ValidateUtils::validateUrl,
                getResources().getString(R.string.url_validation_error));
    }

    @Override
    public void validate(
            TextInputLayout textInputLayout,
            Function<String, Boolean> validationReference,
            String errorResponse) {
        textInputLayout
                .getEditText()
                .addTextChangedListener(
                        new TextWatcher() {

                            @Override
                            public void beforeTextChanged(
                                    CharSequence charSequence, int i, int i1, int i2) {
                                // Nothing here
                            }

                            @Override
                            public void onTextChanged(
                                    CharSequence charSequence, int i, int i1, int i2) {
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
    protected int getTitle() {
        return R.string.create_copyright;
    }

    @Override
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
}
