package com.eventyay.organizer.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LinkHandlerTest {

    private String resetPassUrl = "https://eventyay.com/reset-password?token=12345678";
    private String verifyEmailUrl = "https://eventyay.com/verify?token=12345678";

    @Test
    public void shouldHaveCorrectDestination() {
        assertEquals(
                LinkHandler.Destination.RESET_PASSWORD,
                LinkHandler.getDestinationAndToken(resetPassUrl).getDestination());
        assertEquals(
                LinkHandler.Destination.VERIFY_EMAIL,
                LinkHandler.getDestinationAndToken(verifyEmailUrl).getDestination());
    }

    @Test
    public void shouldGetPasswordResetToken() {
        assertEquals(
                LinkHandler.Destination.RESET_PASSWORD,
                LinkHandler.getDestinationAndToken(resetPassUrl).getDestination());
        assertEquals("12345678", LinkHandler.getDestinationAndToken(resetPassUrl).getToken());
    }

    @Test
    public void shouldGetEmailVerificationToken() {
        assertEquals(
                LinkHandler.Destination.VERIFY_EMAIL,
                LinkHandler.getDestinationAndToken(verifyEmailUrl).getDestination());
        assertEquals("12345678", LinkHandler.getDestinationAndToken(verifyEmailUrl).getToken());
    }
}
