package org.fossasia.openevent.app.common.utils.core;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public final class ErrorUtils {

    private ErrorUtils() {
        // Never Called
    }

    public static String getMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
            return getErrorDetails(responseBody);
        } else {
            return throwable.getMessage();
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static String getErrorDetails(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            JSONObject jsonArray = new JSONObject(jsonObject.getJSONArray("errors").getString(0));
            JSONObject errorSource = new JSONObject(jsonArray.getString("source"));

            String pointedField = getPointedField(errorSource.getString("pointer"));
            if (pointedField == null)
                return jsonArray.get("detail").toString();
            else
                return jsonArray.get("detail").toString().replace(".", "") + ": " + pointedField;

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static String getPointedField(String pointerString) {
        if (pointerString == null)
            return null;
        else {
            String[] path = pointerString.split("/");
            return path[path.length - 1];
        }
    }
}
