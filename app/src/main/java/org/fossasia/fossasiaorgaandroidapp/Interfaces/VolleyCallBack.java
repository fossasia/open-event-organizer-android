package org.fossasia.fossasiaorgaandroidapp.Interfaces;

import com.android.volley.VolleyError;

/**
 * Created by rishabhkhanna on 26/04/17.
 */

public interface VolleyCallBack {
    void onSuccess(String result);
    void onError(VolleyError error);
}
