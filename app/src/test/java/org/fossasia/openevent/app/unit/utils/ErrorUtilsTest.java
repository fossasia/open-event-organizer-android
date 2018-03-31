package org.fossasia.openevent.app.unit.utils;

import android.accounts.Account;

import org.fossasia.openevent.app.utils.ErrorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class ErrorUtilsTest {

    private static String content1;
    private static String content2;
    private static String content3;

    static {
        URI uri1 = null;
        URI uri2 = null;
        URI uri3 = null;

        try {
            uri1 = ErrorUtilsTest.class.getClassLoader().getResource("raw/content1.json").toURI();
            uri2 = ErrorUtilsTest.class.getClassLoader().getResource("raw/content2.json").toURI();
            uri3 = ErrorUtilsTest.class.getClassLoader().getResource("raw/content3.json").toURI();
        } catch (URISyntaxException e) {
            Timber.e(e.getMessage());
        }
        try {
            content1 = new String(Files.readAllBytes(Paths.get(uri1)), Charset.forName("utf-8"));
            content2 = new String(Files.readAllBytes(Paths.get(uri2)), Charset.forName("utf-8"));
            content3 = new String(Files.readAllBytes(Paths.get(uri3)), Charset.forName("utf-8"));
        } catch (IOException e) {
            Timber.e(e.getMessage());
        }
    }

    private static final ResponseBody RESPONSE_BODY_1 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), content1);
    private static final Response<Account> ERROR_RESPONSE = Response.error(400, RESPONSE_BODY_1);
    private static final Response<Account> ERROR_RESPONSE_1 = Response.error(422, RESPONSE_BODY_1);

    private static final ResponseBody RESPONSE_BODY_2 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), content2);
    private static final Response<Account> ERROR_RESPONSE_2 = Response.error(422, RESPONSE_BODY_2);

    private static final ResponseBody RESPONSE_BODY_3 = ResponseBody.create(MediaType.parse("application/vnd.api+json"), content3);
    private static final Response<Account> ERROR_RESPONSE_3 = Response.error(422, RESPONSE_BODY_3);

    private static HttpException httpException = new HttpException(ERROR_RESPONSE);
    private static HttpException httpException1 = new HttpException(ERROR_RESPONSE_1);

    private static IOException ioException = new IOException();

    @Test
    public void shouldReturnNullOnNullAnsEmptyPointedField() throws IOException, URISyntaxException {
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

        assertEquals("Something went wrong! Please check any empty field of a form.", str);
    }

    @Test
    public void shouldReturnOtherThrowableErrorMessageSuccessfully() {
        String str = String.valueOf(ErrorUtils.getMessage(ioException));

        assertEquals("null", str);
    }
}
