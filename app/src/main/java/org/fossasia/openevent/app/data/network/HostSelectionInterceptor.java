package org.fossasia.openevent.app.data.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class HostSelectionInterceptor implements Interceptor {
    private String scheme;
    private String host;

    public HostSelectionInterceptor(){
        //Intentionally left blank
    }

    public void setInterceptor(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        scheme = httpUrl.scheme();
        host = httpUrl.host();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
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
