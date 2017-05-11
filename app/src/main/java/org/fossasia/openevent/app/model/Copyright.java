package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;

public class Copyright {

    @SerializedName("holder")
    private String holder;
    @SerializedName("holder_url")
    private String holderUrl;
    @SerializedName("licence")
    private String license;
    @SerializedName("licence_url")
    private String licenseUrl;
    @SerializedName("logo")
    private String logo;
    @SerializedName("year")
    private Long year;

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getHolderUrl() {
        return holderUrl;
    }

    public void setHolderUrl(String holderUrl) {
        this.holderUrl = holderUrl;
    }

    public String getLicence() {
        return license;
    }

    public void setLicence(String licence) {
        license = licence;
    }

    public String getLicenceUrl() {
        return licenseUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        licenseUrl = licenceUrl;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

}
