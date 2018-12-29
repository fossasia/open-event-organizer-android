package com.eventyay.organizer.data.event;

import lombok.Data;

@Data
public class OriginalImageTemporaryUrl {
    private String url;

    public OriginalImageTemporaryUrl() {
    }

    public OriginalImageTemporaryUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
