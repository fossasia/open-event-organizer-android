package org.fossasia.openevent.app.Api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.fossasia.openevent.app.Interfaces.VolleyCallBack;
import org.fossasia.openevent.app.Utils.CheckLogin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rishabhkhanna on 25/04/17.
 */

public class ApiCall {

    public static final String TAG = "ApiCall";

    public static void callApi(final Context context, String url , final VolleyCallBack callBack){

        RequestQueue  queue = Volley.newRequestQueue(context);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                callBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String token = CheckLogin.isLogin(context);
                params.put("Accept", "application/json");
                Log.d(TAG, "getHeaders: " + token);
                params.put("Authorization" ,"JWT " + token);
                return params;
            }
        };

        int socketTimeout = 90000; // 90 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);

    }

    public static void PostApiCall(final Context context , String url , final VolleyCallBack callBack){

        RequestQueue queue = Volley.newRequestQueue(context);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                callBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String token = CheckLogin.isLogin(context);
                params.put("Accept", "application/json");
                Log.d(TAG, "getHeaders: " + token);
                params.put("Authorization" ,"JWT " + token);
                return params;
            }
        };
        int socketTimeout = 90000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);

    }
}
