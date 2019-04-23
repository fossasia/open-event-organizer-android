package com.eventyay.organizer.core.stripeauth;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import com.eventyay.organizer.databinding.ActivityStripeLoginBinding;

import com.eventyay.organizer.R;

public class StripeLoginActivity extends AppCompatActivity {

    private ActivityStripeLoginBinding binding;
    private static final String CLIENT_ID = "ca_EtBsqw22CSDWlGr263zCqNAavwnusEZY";
    private static final String REDIRECT_URI = "https://eventyay.com/orders/stripe/callback/";
    private static final String REDIRECT_URI_ROOT = "https";
    private static final String CODE = "code";
    public static final String API_SCOPE = "read_write";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String CLIENT_SECRET = "sk_test_ch4DlvkEoc4VhusddZq3YRrz00t6ZRrUHh";

    private static boolean stripeConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stripe_login);

        binding.connectWithStripe.setOnClickListener(view -> connectToStripe());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri data = getIntent().getData();
        if (data != null && !TextUtils.isEmpty(data.getScheme())) {
            if (REDIRECT_URI_ROOT.equals(data.getScheme())) {
                String code = data.getQueryParameter(CODE);
                String error = data.getQueryParameter("error");
                Log.e("onStart", "handle result of authorization with code :" + code);
                if (!TextUtils.isEmpty(code)) {
                    getTokenFormUrl(code);
                    stripeConnected = true;
                    finish();
                }
                if(!TextUtils.isEmpty(error)) {
                    //a problem occurs, the user reject our granting request or something like that
                    Toast.makeText(this, "Error while connecting to Stripe",Toast.LENGTH_LONG).show();
                    Log.e("onStart", "handle result of authorization with error :" + error);
                    //then die
                    finish();
                }
            }
        }
    }

    private void connectToStripe() {

        HttpUrl authorizeUrl = HttpUrl.parse("https://connect.stripe.com/oauth/authorize")
            .newBuilder()
            .addQueryParameter("client_id", CLIENT_ID)
            .addQueryParameter("scope", API_SCOPE)
            .addQueryParameter("redirect_uri", REDIRECT_URI)
            .addQueryParameter("response_type", CODE)
            .build();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
        startActivity(i);
    }

    /**
     * Retrieve the OAuth token
     */
    private void getTokenFormUrl(String code) {
        OAuthServerInterface oAuthServer = RetrofitBuilder.getClient(this);
        Call<OAuthToken> getRequestTokenFormCall = oAuthServer.requestTokenForm(
            CLIENT_SECRET,
            code,
            GRANT_TYPE_AUTHORIZATION_CODE
        );
        getRequestTokenFormCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                Log.e("onResponse", "===============New Call===============");
                Log.e("onResponse", "The call getRequestTokenFormCall succeed with code="
                    + response.code() + " and has body = " + response.body());
                //token received
            }
            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                Log.e("onFailure", "===============New Call===============");
                Log.e("onFailure", "The call getRequestTokenFormCall failed", t);
            }
        });
    }

    public boolean getStripeConnected() {
        return stripeConnected;
    }
}
