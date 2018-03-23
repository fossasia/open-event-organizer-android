package org.fossasia.openevent.app.module.event.copyright.update;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.CopyrightCreateLayoutBinding;
import org.fossasia.openevent.app.module.event.copyright.update.contract.IUpdateCopyrightPresenter;
import org.fossasia.openevent.app.module.event.copyright.update.contract.IUpdateCopyrightView;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class UpdateCopyrightFragment extends BaseBottomSheetFragment<IUpdateCopyrightPresenter> implements IUpdateCopyrightView {

    @Inject
    Lazy<IUpdateCopyrightPresenter> presenterProvider;
    private Validator validator;
    private CopyrightCreateLayoutBinding binding;
    private static Copyright copyright;

    public static UpdateCopyrightFragment newInstance(Copyright copyright) {
        UpdateCopyrightFragment.copyright = copyright;
        return new UpdateCopyrightFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        OrgaApplication.getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.copyright_create_layout, container, false);
        validator = new Validator(binding.form);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                getPresenter().updateCopyright(copyright);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setCopyright(copyright);
    }

    @Override
    public Lazy<IUpdateCopyrightPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.copyright_create_layout;
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
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
