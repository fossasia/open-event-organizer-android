package com.eventyay.organizer.utils;

public class LinkHandler {

    public Destination destination;
    public String token;

    public LinkHandler(Destination destination, String token) {
        this.destination = destination;
        this.token = token;
    }

    public static LinkHandler getDestinationAndToken(String url) {
        if (url.contains("reset-password")) {
            String token = url.substring(url.indexOf('=') + 1);
            return new LinkHandler(Destination.RESET_PASSWORD, token);
        } else if (url.contains("verify")) {
            String token = url.substring(url.indexOf('=') + 1);
            return new LinkHandler(Destination.VERIFY_EMAIL, token);
        } else
            return null;
    }

    public Destination getDestination() {
        return destination;
    }

    public String getToken() {
        return token;
    }

    public enum Destination {
        VERIFY_EMAIL,
        RESET_PASSWORD
    }
}
