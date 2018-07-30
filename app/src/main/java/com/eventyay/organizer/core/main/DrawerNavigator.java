package com.eventyay.organizer.core.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.event.about.AboutEventActivity;
import com.eventyay.organizer.core.event.create.CreateEventActivity;

import static com.eventyay.organizer.core.event.create.CreateEventActivity.EVENT_ID;

class DrawerNavigator {

    private static final String GOOGLE_FORM_LINK = "https://docs.google.com/forms/d/e/" +
    "1FAIpQLSfJ-v1mbmNp1ChpsikHDx6HZ5G9Bq8ELCivckPPcYlOAFOy2Q/viewform?usp=sf_link";

    private final Context context;
    private final FragmentNavigator fragmentNavigator;
    private final OrganizerViewModel organizerViewModel;

    private AlertDialog logoutDialog;

    DrawerNavigator(Context context, FragmentNavigator fragmentNavigator, OrganizerViewModel organizerViewModel) {
        this.context = context;
        this.fragmentNavigator = fragmentNavigator;
        this.organizerViewModel = organizerViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setLogoutDialog(AlertDialog logoutDialog) {
        this.logoutDialog = logoutDialog;
    }

    void selectItem(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutDialog();
        } else if (id == R.id.nav_edit_event) {
            Intent intent = new Intent(context, CreateEventActivity.class);
            intent.putExtra(EVENT_ID, fragmentNavigator.getEventId());
            context.startActivity(intent);
        } else if (id == R.id.nav_about_event) {
            Intent intent = new Intent(context, AboutEventActivity.class);
            intent.putExtra(AboutEventActivity.EVENT_ID, fragmentNavigator.getEventId());
            context.startActivity(intent);
        } else if (id == R.id.nav_suggestion) {
            Intent googleFormIntent = new Intent(Intent.ACTION_VIEW);
            googleFormIntent.setData(Uri.parse(GOOGLE_FORM_LINK));
            context.startActivity(googleFormIntent);
        } else
            fragmentNavigator.loadFragment(id);
    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> organizerViewModel.logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}
