package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;

public class SocialLink {

    @SerializedName("id")
    private Long id;
    @SerializedName("link")
    private String link;
    @SerializedName("name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
