package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class CallForPapers {
    @PrimaryKey
    public long id;

    public String announcement;
    public String endDate;
    public String privacy;
    public String startDate;
    public String timezone;
}
