package org.fossasia.openevent.app.core.event.copyright.update;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.data.copyright.Copyright;
import org.fossasia.openevent.app.databinding.CopyrightCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class UpdateCopyrightFragment extends BaseFragment<UpdateCopyrightPresenter> implements IUpdateCopyrightView {

    private static final String EVENT_ID = "id";

    @Inject
    Lazy<UpdateCopyrightPresenter> presenterProvider;
    private Validator validator;
    private CopyrightCreateLayoutBinding binding;
    private long copyrightId;

    public static UpdateCopyrightFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(EVENT_ID, id);
        UpdateCopyrightFragment updateCopyrightFragment = new UpdateCopyrightFragment();
        updateCopyrightFragment.setArguments(bundle);
        return updateCopyrightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.copyright_create_layout, container, false);
        validator = new Validator(binding.form);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        copyrightId = bundle.getLong(EVENT_ID);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                getPresenter().updateCopyright();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().loadCopyright(copyrightId);
    }

    @Override
    public void dismiss() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setCopyright(Copyright copyright) {
        binding.setCopyright(copyright);
    }

    @Override
    protected int getTitle() {
        return R.string.edit_copyright;
    }

    @Override
    public Lazy<UpdateCopyrightPresenter> getPresenterProvider() {
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
