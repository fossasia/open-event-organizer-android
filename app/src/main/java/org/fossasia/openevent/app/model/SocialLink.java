
package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;



public class SocialLink {

    @SerializedName("id")
    private Long mId;
    @SerializedName("link")
    private String mLink;
    @SerializedName("name")
    private String mName;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
