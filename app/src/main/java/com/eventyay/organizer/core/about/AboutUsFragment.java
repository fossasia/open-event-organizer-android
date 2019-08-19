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
import androidx.fragment.app.FragmentTransaction;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.settings.AcknowledgementDecider;
import com.eventyay.organizer.core.settings.LegalPreferenceFragment;
import com.eventyay.organizer.databinding.AboutUsFragmentBinding;
import com.eventyay.organizer.utils.BrowserUtils;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsFragment extends BaseFragment {

    private AboutUsFragmentBinding binding;
    private final AcknowledgementDecider acknowledgementDecider = new AcknowledgementDecider();

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.about_us_fragment, container, false);

        Element legalElement = new Element();
        legalElement.setTitle("Legal");

        legalElement.setOnClickListener(v -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                .replace(R.id.fragment, LegalPreferenceFragment.newInstance())
                .addToBackStack(null)
                .commit();
        });

        Element developersElement = new Element();
        developersElement.setTitle(getString(R.string.developers));

        developersElement.setOnClickListener(v -> {
            BrowserUtils.launchUrl(getContext(), getString(R.string.github_developers_link));
        });

        Element shareElement = new Element();
        shareElement.setTitle(getString(R.string.share));

        shareElement.setOnClickListener(v -> {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareSub = getString(R.string.msg_check_out);
            String shareBody = getString(R.string.msg_check_this_out) + getString(R.string.play_store_link);
            String shareTitle = getString(R.string.msg_share_using);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, shareTitle));
        });

        Element thirdPartyLicenses = new Element();
        thirdPartyLicenses.setTitle(getString(R.string.third_party_licenses));

        thirdPartyLicenses.setOnClickListener(v -> {
            acknowledgementDecider.openAcknowledgementsSection(getContext());
        });

        AboutPage aboutPage = new AboutPage(getContext())
            .isRTL(false)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.about_us_description))
            .addItem(new Element("Version " + BuildConfig.VERSION_NAME, R.drawable.ic_info))
            .addGroup("Connect with us")
            .addGitHub("fossasia/open-event-organizer-android")
            .addPlayStore(getContext().getPackageName())
            .addWebsite(getString(R.string.FRONTEND_HOST))
            .addFacebook(getString(R.string.FACEBOOK_ID))
            .addTwitter(getString(R.string.TWITTER_ID))
            .addYoutube(getString(R.string.YOUTUBE_ID))
            .addItem(developersElement)
            .addItem(legalElement)
            .addItem(shareElement);

        if (BuildConfig.FLAVOR.equals("playStore")) {
            aboutPage.addItem(thirdPartyLicenses);
        }

        View aboutPageView = aboutPage.create();

        binding.fragment.addView(aboutPageView);

        return binding.getRoot();
    }

    @Override
    protected int getTitle() {
        return R.string.about_us;
    }
}
