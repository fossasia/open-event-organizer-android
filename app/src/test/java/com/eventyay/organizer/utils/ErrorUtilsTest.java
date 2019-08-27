package com.eventyay.organizer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.accounts.Account;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import retrofit2.HttpException;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class ErrorUtilsTest {

    public String contentType = "application/vnd.api+json";

    public URL url1;
    public URL url2;
    public URL url3;
    public URL url4;
    public URL url5;

    public String content1;
    public String content2;
    public String content3;
    public String content4;
    public String content5;

    public HttpException httpException1;
    public HttpException httpException2;
    public HttpException httpException3;
    public HttpException httpException4;
    public HttpException httpException5;

    public ResponseBody responseBody1;
    public ResponseBody responseBody2;
    public ResponseBody responseBody3;
    public ResponseBody responseBody4;
    public ResponseBody responseBody5;

    public Response<Account> errorResponse1;
    public Response<Account> errorResponse2;
    public Response<Account> errorResponse3;
    public Response<Account> errorResponse4;
    public Response<Account> errorResponse5;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        url1 = Resources.getResource("raw/content1.json");
        url2 = Resources.getResource("raw/content2.json");
        url3 = Resources.getResource("raw/content3.json");
        url4 = Resources.getResource("raw/content4.json");
        url5 = Resources.getResource("raw/content5.json");

        try {
            content1 = Resources.toString(url1, Charsets.UTF_8);
            content2 = Resources.toString(url2, Charsets.UTF_8);
            content3 = Resources.toString(url3, Charsets.UTF_8);
            content4 = Resources.toString(url4, Charsets.UTF_8);
            content5 = Resources.toString(url5, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseBody1 = ResponseBody.create(MediaType.parse(contentType), content1);
        errorResponse1 = Response.error(422, responseBody1);

        responseBody2 = ResponseBody.create(MediaType.parse(contentType), content2);
        errorResponse2 = Response.error(422, responseBody2);

        responseBody3 = ResponseBody.create(MediaType.parse(contentType), content3);
        errorResponse3 = Response.error(422, responseBody3);

        responseBody4 = ResponseBody.create(MediaType.parse(contentType), content4);
        errorResponse4 = Response.error(400, responseBody4);

        responseBody5 = ResponseBody.create(MediaType.parse(contentType), content5);
        errorResponse5 = Response.error(400, responseBody5);

        httpException1 = new HttpException(errorResponse1);
        httpException2 = new HttpException(errorResponse2);
        httpException3 = new HttpException(errorResponse3);
        httpException4 = new HttpException(errorResponse4);
        httpException5 = new HttpException(errorResponse5);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldReturnNullOnNullAnsEmptyPointedField()
            throws IOException, URISyntaxException {
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
        String str = String.valueOf(ErrorUtils.getErrorDetails(httpException1));

        assertEquals("Missing data for required field - licence", str);
    }

    @Test
    public void shouldReturnErrorDetailsWithNullOrEmptyPointedFieldSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorDetails(httpException3));
        String str1 = String.valueOf(ErrorUtils.getErrorDetails(httpException2));

        assertEquals("Missing data for required field.", str1);
        assertEquals("Missing data for required field.", str);
    }

    @Test
    public void shouldReturnErrorMessageSuccessfully() {
        String str = String.valueOf(ErrorUtils.getMessage(httpException1));

        assertEquals("Missing data for required field - licence", str);

        str = String.valueOf(ErrorUtils.getMessage(httpException5));

        assertEquals("Access Forbidden: Co-Organizer access required - order_id", str);
    }

    @Test
    public void shouldReturnErrorMessageTitleAndDetailSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorTitleAndDetails(httpException4));

        assertEquals("Bad Request: The URL does not exist", str);
    }

    @Test
    public void shouldReturnErrorMessageTitleDetailAndPointerSuccessfully() {
        String str = String.valueOf(ErrorUtils.getErrorTitleAndDetails(httpException5));

        assertEquals("Access Forbidden: Co-Organizer access required - order_id", str);
    }
}
