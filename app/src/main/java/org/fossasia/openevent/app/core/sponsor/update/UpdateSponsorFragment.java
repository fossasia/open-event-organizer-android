package org.fossasia.openevent.app.core.sponsor.update;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseBottomSheetFragment;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorCreateLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class UpdateSponsorFragment extends BaseBottomSheetFragment<UpdateSponsorPresenter> implements UpdateSponsorView {

    private static final String SPONSOR_ID = "id";

    @Inject
    Lazy<UpdateSponsorPresenter> presenterProvider;
    private Validator validator;
    private SponsorCreateLayoutBinding binding;
    private long sponsorId;

    public static UpdateSponsorFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(SPONSOR_ID, id);
        UpdateSponsorFragment updateSponsorFragment = new UpdateSponsorFragment();
        updateSponsorFragment.setArguments(bundle);
        return updateSponsorFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.sponsor_create_layout, container, false);
        validator = new Validator(binding.form);

        Bundle bundle = getArguments();
        sponsorId = bundle.getLong(SPONSOR_ID);

        binding.submit.setOnClickListener(view -> {
            if (validator.validate())
                getPresenter().updateSponsor();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().loadSponsor(sponsorId);
    }

    @Override
    public void setSponsor(Sponsor sponsor) {
        binding.setSponsor(sponsor);
    }

    @Override
    public Lazy<UpdateSponsorPresenter> getPresenterProvider() {
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
