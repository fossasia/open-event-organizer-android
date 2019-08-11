package com.eventyay.organizer.core.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.databinding.AboutTheAppFragmentBinding;
import com.eventyay.organizer.utils.BrowserUtils;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutTheAppFragment extends BaseFragment {

    private AboutTheAppFragmentBinding binding;

    public static AboutTheAppFragment newInstance() {
        return new AboutTheAppFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.about_the_app_fragment, container, false);

        binding.ossLicenses.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.btnRepo.setOnClickListener(view -> {
            BrowserUtils.launchUrl(getContext(), getString(R.string.github_repo_link));
        });

        binding.btnDevelopers.setOnClickListener(view -> {
            BrowserUtils.launchUrl(getContext(), getString(R.string.github_developers_link));
        });

        return binding.getRoot();
    }

    @Override
    protected int getTitle() {
        return R.string.about_the_app;
    }
}
