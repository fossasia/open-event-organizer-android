package org.fossasia.openevent.app.unit.utils;

import android.accounts.Account;

import org.fossasia.openevent.app.utils.ErrorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = Config.OLDEST_SDK, application = Application.class)
@RunWith(JUnit4.class)
public class ErrorUtilsTest {

    public static final String CONTENT1 = "{\"errors\": [{\"status\": \"422\", \"source\": {\"pointer\": \"/data/attributes/licence\"}, \"detail\": \"Missing data for required field.\", \"title\": \"Validation error\"}], \"jsonapi\": {\"version\": \"1.0\"}}";
    public static final String CONTENT2 = "{\"errors\": [{\"status\": \"422\", \"source\": {\"pointer\": \"\"}, \"detail\": \"Missing data for required field.\", \"title\": \"Validation error\"}], \"jsonapi\": {\"version\": \"1.0\"}}";
    public static final String CONTENT3 = "{\"errors\": [{\"status\": \"422\", \"source\": {}, \"detail\": \"Missing data for required field.\", \"title\": \"Validation error\"}], \"jsonapi\": {\"version\": \"1.0\"}}";

    public static final ResponseBody RESPONSE_BODY_1 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), CONTENT1);
    public static final Response<Account> ERROR_RESPONSE = Response.error(400, RESPONSE_BODY_1);
    public static final Response<Account> ERROR_RESPONSE_1 = Response.error(422, RESPONSE_BODY_1);

    public static final ResponseBody RESPONSE_BODY_2 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), CONTENT2);
    public static final Response<Account> ERROR_RESPONSE_2 = Response.error(422, RESPONSE_BODY_2);

    public static final ResponseBody RESPONSE_BODY_3 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), CONTENT3);
    public static final Response<Account> ERROR_RESPONSE_3 = Response.error(422, RESPONSE_BODY_3);

    public static HttpException httpException = new HttpException(ERROR_RESPONSE);
    public static HttpException httpException1 = new HttpException(ERROR_RESPONSE_1);

    public static IOException ioException = new IOException();

    @Test
    public void shouldReturnNullOnNullAnsEmptyPointedField() {
        assertNull(ErrorUtils.getPointedField(null));
        assertNull(ErrorUtils.getPointedField(""));
    }

    @Test
    public void shouldReturnPointedField() {
        String pointer = "/data/attributes/form/end_field";
        String pointer1 = "/data/attributes/form_field";
        String pointer2 = "/data/attributes/";
        String pointer3 = "/data";

        assertEquals("end_field", ErrorUtils.getPointedField(pointer));
        assertEquals("form_field", ErrorUtils.getPointedField(pointer1));
        assertNull(ErrorUtils.getPointedField(pointer2));
        assertNull(ErrorUtils.getPointedField(pointer3));
    }

    @Test
    public void shouldReturnErrorDetailsWithPointedFieldSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorDetails(ERROR_RESPONSE_1.errorBody()));

        assertEquals("Missing data for required field: licence", str);
        assertNotEquals("Some random string", str);
    }

    @Test
    public void shouldReturnErrorDetailsWithNullPointedFieldSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorDetails(ERROR_RESPONSE_2.errorBody()));

        assertEquals("Missing data for required field.", str);
        assertNotEquals("Some random string", str);
    }

    @Test
    public void shouldReturnErrorDetailsWithEmptyPointedFieldSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorDetails(ERROR_RESPONSE_3.errorBody()));

        assertEquals("Missing data for required field.", str);
        assertNotEquals("Some random string", str);
    }

    @Test
    public void shouldReturnErrorMessageSuccessfully() {
        String str = String.valueOf(ErrorUtils.getMessage(httpException1));

        assertEquals("Missing data for required field: licence", str);
    }

    @Test
    public void shouldReturnStoredErrorMessageSuccessfully() {
        String str = String.valueOf(ErrorUtils.getMessage(httpException));

        assertEquals("Something went wrong! Please check any empty field if a form.", str);
    }

    @Test
    public void shouldNotReturnErrorMessageSuccessfully() {
        String str = String.valueOf(ErrorUtils.getMessage(ioException));

        assertEquals("null", str);
    }
}
