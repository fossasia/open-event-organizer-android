package com.eventyay.organizer.data.session;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.tracks.Track;
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
@Type("session")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@SuppressWarnings("PMD.TooManyFields")
public class Session {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    @Relationship("track")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Track track;

    @Relationship("speaker")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.NO_ACTION)
    public Speaker speaker;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public String title;
    public String subtitle;
    public Integer level;
    public String shortAbstract;
    public String longAbstract;
    public String comments;
    public String language;
    public String slidesUrl;
    public String videoUrl;
    public String audioUrl;
    public String signupUrl;
    public String state;
    public String createdAt;
    public String deletedAt;
    public String submittedAt;
    public String lastModifiedAt;
    public String startsAt;
    public String endsAt;
    public boolean isMailSent;
}
