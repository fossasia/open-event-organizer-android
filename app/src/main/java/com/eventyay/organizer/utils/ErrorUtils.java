package com.eventyay.organizer.utils;

import com.eventyay.organizer.data.error.Error;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;

public final class ErrorUtils {

    public static final String ERRORS = "errors";
    public static final String SOURCE = "source";
    public static final String POINTER = "pointer";
    public static final String DETAIL = "detail";
    public static final String TITLE = "title";

    public static final int POINTER_LENGTH = 3;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;

    private ErrorUtils() {
        // Never Called
    }

    public static Error getMessage(Throwable throwable) {
        Error error = new Error();
        if (throwable instanceof HttpException) {
            int errorCode = ((HttpException) throwable).code();

            if (errorCode == BAD_REQUEST || errorCode == UNAUTHORIZED || errorCode == FORBIDDEN || errorCode == NOT_FOUND ||
                errorCode == METHOD_NOT_ALLOWED || errorCode == REQUEST_TIMEOUT) {
                error = getErrorTitleAndDetails(throwable);
            } else if (errorCode == UNPROCESSABLE_ENTITY || errorCode == CONFLICT) {
                error = getErrorDetails(throwable);
            } else {
                error.setDetail(throwable.getMessage());
                return error;
            }
        }

        if (Utils.isEmpty(error.getDetail()))
            error.setDetail(throwable.getMessage());

        return error;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public static Error getErrorDetails(Throwable throwable) {
        Error error = new Error();
        if (throwable instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) throwable).response().errorBody();

            try {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                JSONObject jsonArray = new JSONObject(jsonObject.getJSONArray(ERRORS).get(0).toString());
                JSONObject errorSource = new JSONObject(jsonArray.get(SOURCE).toString());

                try {
                    String pointedField = getPointedField(errorSource.getString(POINTER));
                    if (pointedField == null) {
                        error.setDetail(jsonArray.get(DETAIL).toString());
                    } else {
                        error.setPointer(pointedField);
                        error.setDetail(jsonArray.get(DETAIL).toString().replace(".", ""));
                    }

                } catch (Exception e) {
                    error.setDetail(jsonArray.get(DETAIL).toString());
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return error;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public static Error getErrorTitleAndDetails(Throwable throwable) {
        Error error = new Error();
        if (throwable instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) throwable).response().errorBody();

            try {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                JSONObject jsonArray = new JSONObject(jsonObject.getJSONArray(ERRORS).get(0).toString());
                JSONObject errorSource = new JSONObject(jsonArray.get(SOURCE).toString());

                try {
                    String pointedField = getPointedField(errorSource.getString(POINTER));

                    if (pointedField == null) {
                        error.setDetail(jsonArray.get(DETAIL).toString());
                    } else {
                        error.setPointer(pointedField);
                        error.setDetail(jsonArray.get(DETAIL).toString().replace(".", ""));
                    }
                    error.setTitle(jsonArray.get(TITLE).toString());

                } catch (Exception e) {
                    error.setTitle(jsonArray.get(TITLE).toString());
                    error.setDetail(jsonArray.get(DETAIL).toString());
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return error;
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
