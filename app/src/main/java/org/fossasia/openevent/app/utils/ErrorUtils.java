package org.fossasia.openevent.app.utils;

import retrofit2.HttpException;

public final class ErrorUtils {

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
                    return "Something went wrong! Please check any empty field of a form.";
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
                    return throwable.getMessage();
            }
        }
        return throwable.getMessage();
    }
}
