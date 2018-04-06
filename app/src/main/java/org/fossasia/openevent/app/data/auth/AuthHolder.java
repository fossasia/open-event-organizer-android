package org.fossasia.openevent.app.data.auth;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.auth.model.User;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.utils.JWTUtils;
import org.json.JSONException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthHolder {

    private static final String SHARED_PREFS_TOKEN = "token";
    private final Preferences sharedPreferenceModel;

    private String token;

    @Inject
    public AuthHolder(Preferences sharedPreferenceModel) {
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public String getToken() {
        if (token != null)
            return token;

        return sharedPreferenceModel.getString(SHARED_PREFS_TOKEN, null);
    }

    public String getAuthorization() {
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
