package com.eventyay.organizer.utils;

import androidx.collection.SparseArrayCompat;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class JWTUtilsTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String BODY = "{\"nbf\":1495745400,\"iat\":1495745400,\"exp\":1495745800,\"identity\":344}";
    private static final String UNEXPIRABLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

    @Test
    public void shouldParseJWT() {
        SparseArrayCompat<String> decoded = JWTUtils.decode(TOKEN);

        assertEquals(HEADER, decoded.get(0));
        assertEquals(BODY, decoded.get(1));
    }

    @Test
    public void shouldParseExpiry() throws JSONException {
        long timestamp1 = JWTUtils.getExpiry(TOKEN);

        assertEquals(timestamp1, 1495745800, 0);

        long timestamp2 = JWTUtils.getExpiry(UNEXPIRABLE_TOKEN);

        assertEquals(timestamp2, 2495831800.0, 0);
    }

    @Test
    public void shouldParseIdentity() throws JSONException {
        int id = JWTUtils.getIdentity(TOKEN);

        assertEquals(344, id);

        int id2 = JWTUtils.getIdentity(UNEXPIRABLE_TOKEN);

        assertEquals(344, id2);
    }

    @Test
    public void shouldJudgeExpiry() {
        assertFalse(JWTUtils.isExpired(UNEXPIRABLE_TOKEN));

        assertTrue(JWTUtils.isExpired(TOKEN));
    }

    @Test
    public void shouldFormatToken() {
        String token = "token";

        assertEquals("JWT token", JWTUtils.getAuthorization(token));
    }
}
