package org.fossasia.openevent.app.common;

import org.fossasia.openevent.app.data.models.User;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import timber.log.Timber;

public class ContextManager {

    private static String currency = "$";

    @Inject
    public ContextManager() {}

    public void setOrganiser(User user) {
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

    public void clearOrganiser() {
        Sentry.clearContext();
    }

    public static void setCurrency(String currency) {
        ContextManager.currency = currency;
    }

    public static String getCurrency() {
        return currency;
    }
}
