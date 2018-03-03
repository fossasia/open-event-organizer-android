package org.fossasia.openevent.app.common.data.models;

import android.databinding.ObservableBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@Type("event-copyright")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Copyright {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public String holder;
    public String holderUrl;
    public String licence;
    public String licenceUrl;
    public String year;
    public String logoUrl;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    // Non model entities

    @JsonIgnore
    public final ObservableBoolean creating = new ObservableBoolean();
    @JsonIgnore
    public final ObservableBoolean deleting = new ObservableBoolean();
}
