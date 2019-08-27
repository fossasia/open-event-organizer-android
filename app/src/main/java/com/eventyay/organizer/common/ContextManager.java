package com.eventyay.organizer.common;

import androidx.databinding.ObservableField;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.user.User;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class ContextManager {

    private static final ObservableField<String> CURRENCY = new ObservableField<>("$");
    private static Event selectedEvent;

    private User organiser;

    @Inject
    public ContextManager() {}

    public void setOrganiser(User user) {
        this.organiser = user;

        Map<String, Object> userData = new ConcurrentHashMap<>();
        userData.put("details", user);

        Timber.i("User logged in - %s", user);
        Sentry.getContext()
                .setUser(
                        new UserBuilder()
                                .setEmail(user.getEmail())
                                .setId(String.valueOf(user.getId()))
                                .setData(userData)
                                .build());
    }

    public User getOrganiser() {
        return organiser;
    }

    public void clearOrganiser() {
        Sentry.clearContext();
    }

    public static void setCurrency(String currency) {
        ContextManager.CURRENCY.set(currency);
    }

    public static ObservableField<String> getCurrency() {
        return CURRENCY;
    }

    public static void setSelectedEvent(Event event) {
        selectedEvent = event;
    }

    public static Event getSelectedEvent() {
        return selectedEvent;
    }
}
