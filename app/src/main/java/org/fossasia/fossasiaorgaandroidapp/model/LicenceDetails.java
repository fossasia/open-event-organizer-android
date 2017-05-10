
package org.fossasia.fossasiaorgaandroidapp.model;


import com.google.gson.annotations.SerializedName;



public class LicenceDetails {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("licence_compact_logo")
    private String mLicenceCompactLogo;
    @SerializedName("licence_logo")
    private String mLicenceLogo;
    @SerializedName("licence_url")
    private String mLicenceUrl;
    @SerializedName("long_name")
    private String mLongName;
    @SerializedName("name")
    private String mName;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLicenceCompactLogo() {
        return mLicenceCompactLogo;
    }

    public void setLicenceCompactLogo(String licenceCompactLogo) {
        mLicenceCompactLogo = licenceCompactLogo;
    }

    public String getLicenceLogo() {
        return mLicenceLogo;
    }

    public void setLicenceLogo(String licenceLogo) {
        mLicenceLogo = licenceLogo;
    }

    public String getLicenceUrl() {
        return mLicenceUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        mLicenceUrl = licenceUrl;
    }

    public String getLongName() {
        return mLongName;
    }

    public void setLongName(String longName) {
        mLongName = longName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
