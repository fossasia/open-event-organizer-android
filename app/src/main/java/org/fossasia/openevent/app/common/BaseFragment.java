package org.fossasia.openevent.app.common;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.app.OrgaApplication;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment {

    protected void setTitle(String title) {
        Activity activity = getActivity();

        if (activity != null && activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(title);
            else
                Timber.e("No ActionBar found in Activity %s for Fragment %s", activity, this);
        } else {
            Timber.e("Fragment %s is not attached to any Activity", this);
        }

    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OrgaApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
