package com.eventyay.organizer.core.stripeauth;

import com.google.gson.annotations.SerializedName;

public class OAuthToken {

    @SerializedName("stripe_user_id") private String stripeUserId;
    @SerializedName("stripe_publishable_key") private String stripePublishableKey;
    @SerializedName("scope") private String scope;
    @SerializedName("livemode") private String livemode;
    @SerializedName("token_type") private String tokenType;
    @SerializedName("refresh_token") private String refreshToken;
    @SerializedName("access_token") private String accessToken;

    public OAuthToken() {}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStripeUserId() {
        return stripeUserId;
    }

    public void setStripeUserId(String stripeUserId) {
        this.stripeUserId = stripeUserId;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
