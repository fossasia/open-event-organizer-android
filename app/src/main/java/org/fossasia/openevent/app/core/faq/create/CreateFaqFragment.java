package org.fossasia.openevent.app.core.faq.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.databinding.FaqCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class CreateFaqFragment extends BaseFragment implements CreateFaqView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateFaqViewModel createFaqViewModel;
    private FaqCreateLayoutBinding binding;
    private Validator validator;

    public static CreateFaqFragment newInstance() {
        return new CreateFaqFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.faq_create_layout, container, false);
        createFaqViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateFaqViewModel.class);
        validator = new Validator(binding.form);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createFaqViewModel.getProgress().observe(this, this::showProgress);
        createFaqViewModel.getDismiss().observe(this,(dismiss) -> dismiss());
        createFaqViewModel.getSuccess().observe(this, this::onSuccess);
        createFaqViewModel.getError().observe(this, this::showError);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                createFaqViewModel.createFaq();
        });

        binding.setFaq(createFaqViewModel.getFaq());
    }

    @Override
    protected int getTitle() {
        return R.string.create_faq;
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

    public void dismiss() {
        getFragmentManager().popBackStack();
    }
}
