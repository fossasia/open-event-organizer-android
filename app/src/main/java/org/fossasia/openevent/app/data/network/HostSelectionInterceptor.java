package org.fossasia.openevent.app.data.network;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class HostSelectionInterceptor implements Interceptor {
    private String scheme;
    private String host;

    @Inject
    public HostSelectionInterceptor() {
        //Intentionally left blank
    }

    public void setInterceptor(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null)
            return;
        scheme = httpUrl.scheme();
        host = httpUrl.host();
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        // If new Base URL is properly formatted than replace with old one
        if (scheme != null && host != null) {
            HttpUrl newUrl = original.url().newBuilder()
                .scheme(scheme)
                .host(host)
                .build();
            original = original.newBuilder()
                .url(newUrl)
                .build();
        }
        return chain.proceed(original);
    }
}
