package org.fossasia.openevent.app.core.event.create;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return EventDetailsStepOne.newInstance();
            case 1:
                return EventDetailsStepTwo.newInstance();
            case 2:
                return EventDetailsStepThree.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
