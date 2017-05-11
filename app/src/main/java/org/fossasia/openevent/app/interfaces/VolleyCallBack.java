package org.fossasia.openevent.app.interfaces;

import com.android.volley.VolleyError;

public interface VolleyCallBack {
    void onSuccess(String result);
    void onError(VolleyError error);
}
