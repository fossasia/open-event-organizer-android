package org.fossasia.openevent.app.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.fossasia.openevent.app.OrgaProvider;

import javax.inject.Inject;

public class ConnectionStatusImpl implements ConnectionStatus {

    private Context context;

    public ConnectionStatusImpl() {
        // Do nothing
    }

    @Inject
    public ConnectionStatusImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean isConnected() {
        if (context != null)
            return isConnected(context);
        return isConnectedPure();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }

    public static boolean isConnectedPure() {
        return isConnected(OrgaProvider.context);
    }

}
