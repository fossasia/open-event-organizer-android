package com.eventyay.organizer.data.auth;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.utils.JWTUtils;
import org.json.JSONException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AuthHolder {

    private static final String SHARED_PREFS_TOKEN = "token";
    private final Preferences sharedPreferenceModel;

    private String token;

    @Inject
    public AuthHolder(Preferences sharedPreferenceModel) {
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public String getToken() {
        if (token == null)
            token = sharedPreferenceModel.getString(SHARED_PREFS_TOKEN, null);

        return token;
    }

    @VisibleForTesting
    String getTokenRaw() {
        return token;
    }

    public String getAuthorization() {
        String token = getToken();
        if (token == null)
            return null;
        return JWTUtils.getAuthorization(token);
    }

    public int getIdentity() {
        String token = getToken();

        if (token == null)
            return -1;

        try {
            return JWTUtils.getIdentity(token);
        } catch (JSONException e) {
            return -1;
        }
    }

    public boolean isUser(User user) {
        return user.getId() == getIdentity();
    }

    public boolean isLoggedIn() {
        String token = getToken();

        return token != null && !JWTUtils.isExpired(token);
    }

    private void setToken(String token) {
        this.token = token;
        sharedPreferenceModel.saveString(SHARED_PREFS_TOKEN, token);
    }

    void login(@NonNull String token) {
        setToken(token);
    }

    void logout() {
        setToken(null);
        sharedPreferenceModel.setLong(MainActivity.EVENT_KEY, -1);
        ContextManager.setSelectedEvent(null);
    }

    void saveEmail(String email) {
        sharedPreferenceModel.addStringSetElement(Constants.SHARED_PREFS_SAVED_EMAIL, email);
    }
}
