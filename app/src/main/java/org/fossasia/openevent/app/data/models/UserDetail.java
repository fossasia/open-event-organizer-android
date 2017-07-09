package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@Table(database = OrgaDatabase.class, allFields = true)
public class UserDetail {
    @PrimaryKey(autoincrement = true)
    public int id;

    public String contact;
    public String twitter;
    @JsonProperty("firstname")
    public String firstName;
    public String avatar;
    public String facebook;
    @JsonProperty("lastname")
    public String lastName;
    public String details;
}
