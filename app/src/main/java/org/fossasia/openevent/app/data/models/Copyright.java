package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Copyright {
    @PrimaryKey
    public long id;

    public String holder;
    public String holderUrl;
    @JsonProperty("licence")
    public String license;
    @JsonProperty("licence_url")
    public String licenseUrl;
    public String logo;
    public long year;
}
