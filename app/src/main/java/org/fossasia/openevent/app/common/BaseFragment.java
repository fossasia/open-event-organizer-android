package org.fossasia.openevent.app.common;

import android.support.v4.app.Fragment;

import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.app.OrgaApplication;

public abstract class BaseFragment extends Fragment {

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OrgaApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
