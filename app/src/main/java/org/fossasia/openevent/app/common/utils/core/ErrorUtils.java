package org.fossasia.openevent.app.common.utils.core;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public final class ErrorUtils {

    public static final String ERRORS = "errors";
    public static final String SOURCE = "source";
    public static final String POINTER = "pointer";
    public static final String DETAIL = "detail";
    public static final int POINTER_LENGTH = 3;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int REQUEST_TIMEOUT = 408;

    private ErrorUtils() {
        // Never Called
    }

    public static String getMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case BAD_REQUEST:
                    return "Something went wrong! Please check any empty field if a form.";
                case UNAUTHORIZED:
                    return "Invalid Credentials! Please check your credentials.";
                case FORBIDDEN:
                    return "Sorry, you are not authorized to make this request.";
                case NOT_FOUND:
                    return "Sorry, we couldn't find what you were looking for.";
                case METHOD_NOT_ALLOWED:
                    return "Sorry, this request is not allowed.";
                case REQUEST_TIMEOUT:
                    return "Sorry, request timeout. Please retry after some time.";
                default:
                    ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
                    return getErrorDetails(responseBody);
            }

        } else {
            return throwable.getMessage();
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public static String getErrorDetails(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            JSONObject jsonArray = new JSONObject(jsonObject.getJSONArray(ERRORS).getString(0));
            JSONObject errorSource = new JSONObject(jsonArray.getString(SOURCE));

            try {
                String pointedField = getPointedField(errorSource.getString(POINTER));

                if (pointedField == null)
                    return jsonArray.get(DETAIL).toString();
                else
                    return jsonArray.get(DETAIL).toString().replace(".", "") + ": " + pointedField;
            } catch (Exception e) {
                return jsonArray.get(DETAIL).toString();
            }

        } catch (Exception e) {
            return null;
        }
    }

    public static String getPointedField(String pointerString) {
        if (pointerString == null || Utils.isEmpty(pointerString))
            return null;
        else {
            String[] path = pointerString.split("/");
            if (path.length > POINTER_LENGTH)
                return path[path.length - 1];
            else
                return null;
        }
    }
}
