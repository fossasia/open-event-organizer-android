package org.fossasia.openevent.app.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.utils.JWTUtils;
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

    void saveEncryptedEmail(String email) {
        sharedPreferenceModel.saveString(Constants.PREF_USER_EMAIL, email);
    }

    void saveEncryptedPassword(String password) {
        sharedPreferenceModel.saveString(Constants.PREF_USER_PASSWORD, password);
    }

    public String getEmail() {
        return sharedPreferenceModel.getString(Constants.PREF_USER_EMAIL, null);
    }

    public String getPassword() {
        return sharedPreferenceModel.getString(Constants.PREF_USER_PASSWORD, null);
    }
}
