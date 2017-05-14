package org.fossasia.openevent.app.data.network.interfaces;

import com.android.volley.VolleyError;

public interface VolleyCallBack {
    void onSuccess(String result);
    void onError(VolleyError error);
}
