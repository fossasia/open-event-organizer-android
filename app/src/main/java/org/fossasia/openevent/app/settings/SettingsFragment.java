package org.fossasia.openevent.app.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.DateUtils;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Inject
    RxSharedPreferences sharedPreferences;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle bundle, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(getContext())
            .inject(this);

        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_bottom_surface));
        }

        sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
            .asObservable()
            .distinctUntilChanged()
            .subscribe(DateUtils::setShowLocalTimeZone);

        return view;
    }
}
