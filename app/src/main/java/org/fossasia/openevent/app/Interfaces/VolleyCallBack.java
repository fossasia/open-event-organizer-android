package org.fossasia.openevent.app.Interfaces;

import com.android.volley.VolleyError;



public interface VolleyCallBack {
    void onSuccess(String result);
    void onError(VolleyError error);
}
