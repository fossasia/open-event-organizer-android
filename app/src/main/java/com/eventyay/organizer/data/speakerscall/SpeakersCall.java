package com.eventyay.organizer.data.speakerscall;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.event.Event;
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Type("speakers-call")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class SpeakersCall {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    @Relationship("event")
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public String announcement;
    public String hash;
    public String privacy;
    public String startsAt;
    public String endsAt;
}
