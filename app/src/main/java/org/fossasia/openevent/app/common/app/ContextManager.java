package org.fossasia.openevent.app.common.app;

import android.databinding.ObservableField;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import timber.log.Timber;

public class ContextManager {

    private static final ObservableField<String> currency = new ObservableField<>("$");
    private static Event selectedEvent;

    private User organiser;

    @Inject
    public ContextManager() {}

    public void setOrganiser(User user) {
        this.organiser = user;

        Map<String, Object> userData = new HashMap<>();
        userData.put("details", user);

        Timber.i("User logged in - %s", user);
        Sentry.getContext().setUser(
            new UserBuilder()
            .setEmail(user.getEmail())
            .setId(String.valueOf(user.getId()))
            .setData(userData)
            .build()
        );
    }

    public User getOrganiser() {
        return organiser;
    }

    public void clearOrganiser() {
        Sentry.clearContext();
    }

    public static void setCurrency(String currency) {
        ContextManager.currency.set(currency);
    }

    public static ObservableField<String> getCurrency() {
        return currency;
    }

    public static void setSelectedEvent(Event event) {
        selectedEvent = event;
    }

    public static Event getSelectedEvent() {
        return selectedEvent;
    }
}
