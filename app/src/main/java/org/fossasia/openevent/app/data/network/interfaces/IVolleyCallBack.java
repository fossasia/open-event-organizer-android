package org.fossasia.openevent.app.data.network.interfaces;

import com.android.volley.VolleyError;

public interface IVolleyCallBack {
    void onSuccess(String result);
    void onError(VolleyError error);
}
