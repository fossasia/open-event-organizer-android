package org.fossasia.openevent.app.core.event.copyright;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.about.AboutEventFragment;
import org.fossasia.openevent.app.databinding.CopyrightCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateCopyrightFragment extends BaseFragment<CreateCopyrightPresenter> implements ICreateCopyrightView {

    @Inject
    Lazy<CreateCopyrightPresenter> presenterProvider;
    private CopyrightCreateLayoutBinding binding;
    private Validator validator;

    public static CreateCopyrightFragment newInstance() {
        return new CreateCopyrightFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.copyright_create_layout, container, false);
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
                getPresenter().createCopyright();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setCopyright(getPresenter().getCopyright());

        validate(binding.form.copyrightHolderUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.copyrightLicenceUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
        validate(binding.form.copyrightLogoUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));
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
    protected int getTitle() {
        return R.string.copyright;
    }

    @Override
    public void dismiss() {
        getFragmentManager().beginTransaction()
            .replace(R.id.fragment, AboutEventFragment.newInstance(getPresenter().getParentEventId()))
            .commit();
    }

    @Override
    public Lazy<CreateCopyrightPresenter> getPresenterProvider() {
        return presenterProvider;
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
