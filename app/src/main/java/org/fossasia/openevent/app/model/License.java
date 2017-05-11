package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;

public class License {

    @SerializedName("description")
    private String description;
    @SerializedName("licence_compact_logo")
    private String licenceCompactLogo;
    @SerializedName("licence_logo")
    private String licenseLogo;
    @SerializedName("licence_url")
    private String licenseUrl;
    @SerializedName("long_name")
    private String longName;
    @SerializedName("name")
    private String name;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicenceCompactLogo() {
        return licenceCompactLogo;
    }

    public void setLicenceCompactLogo(String licenceCompactLogo) {
        this.licenceCompactLogo = licenceCompactLogo;
    }

    public String getLicenceLogo() {
        return licenseLogo;
    }

    public void setLicenceLogo(String licenceLogo) {
        licenseLogo = licenceLogo;
    }

    public String getLicenceUrl() {
        return licenseUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        licenseUrl = licenceUrl;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
