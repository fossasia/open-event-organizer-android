package org.fossasia.openevent.app.common.utils.ui;

import android.app.Activity;
import android.widget.Toast;

import javax.inject.Inject;

public class BackPressHandler {

    private static final int BACK_PRESS_RESET_TIME = 2000;
    private long backPressed;

    @Inject
    public BackPressHandler() { }

    public void onBackPressed(Activity activity, Runnable action) {
        if (backPressed + BACK_PRESS_RESET_TIME > System.currentTimeMillis()) {
            action.run();
        } else {
            Toast.makeText(activity, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

}
