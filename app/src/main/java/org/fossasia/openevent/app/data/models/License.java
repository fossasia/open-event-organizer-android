package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class License extends BaseModel {

    @PrimaryKey
    private long id;

    private String name;
    private String description;
    @SerializedName("licence_compact_logo")
    private String licenceCompactLogo;
    @SerializedName("licence_logo")
    private String licenseLogo;
    @SerializedName("licence_url")
    private String licenseUrl;
    @SerializedName("long_name")
    private String longName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getLicenseLogo() {
        return licenseLogo;
    }

    public void setLicenseLogo(String licenseLogo) {
        this.licenseLogo = licenseLogo;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
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
